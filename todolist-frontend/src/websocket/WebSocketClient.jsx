import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { https_taskflow, WEBSOCKET_URL } from "../service/api";

import { EVENT } from "../event/EventType";
import { useNotificationContext } from "../context/NotificationContext";
import { useProjectContext } from "../context/ProjectContext";
import {handleProjectEvent} from "./handlers/project.handler";
import {handleMemberEvent} from "./handlers/member.handler";
import {useNavigate} from "react-router-dom";
import {handleSectionEvent} from "./handlers/section.handler";
import {handleTaskEvent} from "./handlers/task.handler";

export function WebSocketClient() {

    const { setProjects, activeProject, setActiveProject, setMembers, setSections, setTaskDetail, activeTaskId } = useProjectContext();

    const activeTaskIdRef = useRef(activeTaskId);

    const navigate = useNavigate();

    const currentUserId = JSON.parse(localStorage.getItem("USER_INFO"))?.id;

    const {
        setNotifications,
        setCountNotificationsUnRead,
        pageSizeNotification,
        setNewNotificationFromWebsocket
    } = useNotificationContext();

    const token = JSON.parse(localStorage.getItem("USER_INFO"))?.token;

    const actorId = JSON.parse(localStorage.getItem("USER_INFO"))?.id;

    const stompClientRef = useRef(null);
    const projectSubscriptionsRef = useRef([]);

    /* ================= CONNECT SOCKET ================= */

    // update ref khi state thay đổi
    useEffect(() => {
        activeTaskIdRef.current = activeTaskId;
    }, [activeTaskId]);

    useEffect(() => {
        if (!token) return;

        const socket = new SockJS(WEBSOCKET_URL);

        const client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${token}`,
            },
            debug: (str) => console.log("[STOMP]", str),

            onConnect: () => {
                console.log("✅ WebSocket connected");

                // notification cá nhân
                client.subscribe("/user/queue/notification", (msg) => {
                    const event = JSON.parse(msg.body);
                    handleNotification(event);
                });

                // project summary (list project)
                client.subscribe("/user/queue/project-summary", (msg) => {
                    const event = JSON.parse(msg.body);
                    handleProjectSummaryEvent(event);
                });
            },

            onStompError: (frame) => {
                console.error("❌ STOMP error:", frame.headers["message"]);
            },
        });

        client.activate();
        stompClientRef.current = client;

        return () => {
            console.log("🧹 WebSocket disconnected");
            client.deactivate();
        };
    }, [token]);

    /* ================= SWITCH PROJECT (LIKE selectProject) ================= */
    useEffect(() => {
        const client = stompClientRef.current;
        if (!client || !client.connected) return;

        // 🚪 leave project cũ
        leaveProject();

        if (!activeProject) return;

        // 🚀 enter project mới
        projectSubscriptionsRef.current.push(
            client.subscribe(`/topic/project/${activeProject.id}`, (m) => {
                // console.log("📌 project", m.body)
                const result = handleProjectEvent(
                    JSON.parse(m.body),
                    {activeProject, setActiveProject}
                );
                if (result?.type === "LEAVE_PROJECT") {
                    leaveProject();
                }
            }),
            client.subscribe(`/topic/project/${activeProject.id}/task`, (m) => {

                // console.log("📌 task", m.body)

                handleTaskEvent(JSON.parse(m.body), {
                    activeProject,
                    setSections,
                    activeTaskId: activeTaskIdRef.current, // luôn lấy giá trị mới
                    setTaskDetail,
                    actorId
                });
            }),
            client.subscribe(`/topic/project/${activeProject.id}/section`, (m) => {
                // console.log("📌 section", m.body)

                handleSectionEvent( JSON.parse(m.body), {activeProject, setSections} );
            }),
            client.subscribe(`/topic/project/${activeProject.id}/member`, (m) => {
                // console.log("📌 member", m.body)

                handleMemberEvent(
                    JSON.parse(m.body),
                    {
                        activeProject,
                        setMembers,
                        currentUserId,
                        navigate,
                    }
                )


            }),
            client.subscribe(`/topic/project/${activeProject.id}/comment`, (m) =>
                console.log("📌 comment", m.body)
            )
        );

        console.log("➡️ Entered project", activeProject.id);

        return () => {
            leaveProject();
        };
    }, [activeProject]);

    /* ================= LEAVE PROJECT ================= */
    function leaveProject() {
        projectSubscriptionsRef.current.forEach((s) => s.unsubscribe());
        projectSubscriptionsRef.current = [];
    }

    /* ================= PROJECT HANDLER ================= */
    async function handleProjectSummaryEvent(event) {
        const type = event.eventType;
        const payload = event.payload;

        switch (type) {
            case EVENT.PROJECT_UPDATED: {
                const project = payload?.project;
                if (!project) return;

                setProjects(prev =>
                    prev.map(p =>
                        p.id === project.id ? { ...p, ...project } : p
                    )
                );
                break;
            }

            case EVENT.PROJECT_MEMBER_REMOVED:
            case EVENT.PROJECT_DELETED: {
                const projectId = payload?.projectId || payload?.project?.id;
                if (!projectId) return;

                setProjects(prev =>
                    prev.filter(p => p.id !== projectId)
                );
                break;
            }

            case EVENT.PROJECT_ARCHIVED: {
                const project = payload?.project;
                if (!project) return;

                setProjects(prev => {
                    // 📦 archive → remove khỏi list đang active
                    if (project.isArchived === true) {
                        return prev.filter(p => p.id !== project.id);
                    }

                    // 🔄 unarchive → add lại
                    return [project, ...prev];
                });

                break;
            }

            case EVENT.PROJECT_MEMBER_ACCEPTED: {
                const projectId = payload?.projectId;
                if (!projectId) return;

                const project = await fetchProjectSummary(projectId);
                if (!project) return;

                setProjects(prev => {
                    if (prev.some(p => p.id === project.id)) return prev;
                    return [project, ...prev];
                });
                break;
            }

            default:
                break;
        }
    }

    async function fetchProjectSummary(projectId) {
        try {
            const res = await https_taskflow(`/v1/projects/${projectId}`);
            return res.data.data;
        } catch (e) {
            console.error("Fetch project summary failed", e);
            return null;
        }
    }

    /* ================= NOTIFICATION ================= */
    function handleNotification(notification) {
        setCountNotificationsUnRead(prev => prev + 1);

        setNotifications(prev => {
            const list = [notification, ...prev];
            return list.slice(0, pageSizeNotification);
        });

        setNewNotificationFromWebsocket(notification);
    }

    return null;
}
