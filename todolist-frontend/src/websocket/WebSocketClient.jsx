import {useContext, useEffect} from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import {https_taskflow, WEBSOCKET_URL} from "../service/api";
import {useAppContext} from "../layout/MainLayout";
import AppContext from "antd/es/app/context";
import {EVENT} from "../event/EventType";

export function WebSocketClient() {


    const {
        setCountNotificationsUnRead,
        setNotifications,
        pageSizeNotification,
        setNewNotificationFormWebsocket,
        setProjects,
    } = useAppContext();

    const token = JSON.parse(localStorage.getItem("USER_INFO"))?.token;

    useEffect(() => {
        if (!token) return;

        // 1️⃣ tạo socket
        const socket = new SockJS(WEBSOCKET_URL);

        // 2️⃣ tạo stomp client
        const client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${token}`,
            },
            debug: (str) => console.log("[STOMP]", str),
            // reconnectDelay: 3000,

            onConnect: () => {
                console.log("✅ WebSocket connected");

                // 3️⃣ subscribe user notification
                client.subscribe("/user/queue/notification", (msg) => {
                    const event = JSON.parse(msg.body);
                    handleNotification(event);
                });

                // 4️⃣ subscribe project summary
                client.subscribe("/user/queue/project-summary", (msg) => {
                    const event = JSON.parse(msg.body);
                    handleProjectSummaryEvent(event);
                });
            },

            onStompError: (frame) => {
                console.error("❌ STOMP error:", frame.headers["message"]);
            },
        });

        // 5️⃣ connect
        client.activate();

        return () => {
            console.log("🧹 WebSocket disconnected");
            client.deactivate();
        };
    }, [token]);


    /* ================= PROJECT HANDLER ================= */

    async function handleProjectSummaryEvent(event) {
        const type = event.eventType;
        const payload = event.payload;

        switch (type) {

            case EVENT.PROJECT_CREATED: {
                const project = payload?.project;
                if (!project) return;

                setProjects(prev => {
                    if (prev.some(p => p.id === project.id)) return prev;
                    return [project, ...prev];
                });
                break;
            }

            case EVENT.PROJECT_UPDATED: {
                const project = payload?.project;
                if (!project) return;

                setProjects(prev =>
                    prev.map(p =>
                        p.id === project.id ? {...p, ...project} : p
                    )
                );
                break;
            }

            case EVENT.PROJECT_MEMBER_REMOVED:
            case EVENT.PROJECT_ARCHIVED:
            case EVENT.PROJECT_DELETED: {
                const projectId = payload?.projectId || payload?.project?.id;
                if (!projectId) return;

                setProjects(prev =>
                    prev.filter(p => p.id !== projectId)
                );
                break;
            }

            case EVENT.PROJECT_MEMBER_ACCEPTED: {
                const projectId = payload?.projectId;
                if (!projectId) return;

                // 🔥 async fetch ở NGOÀI
                const project = await fetchProjectSummary(projectId);
                if (!project) return;

                console.log(project)

                setProjects(prev => {
                    if (prev.some(p => p.id === project.id)) return prev;
                    return [project, ...prev];
                });
                break;
            }

            default:
                break;
        }

        console.log(`📦 Project summary updated: ${type}`);
    }


    async function fetchProjectSummary(projectId) {
        try {
            const res = await https_taskflow(`/v1/projects/${projectId}`);
            return await res.data.data;
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

        setNewNotificationFormWebsocket(notification);
    }



    return null;
}
