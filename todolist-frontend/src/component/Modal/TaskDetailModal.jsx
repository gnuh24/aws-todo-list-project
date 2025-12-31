import {
    LockOutlined
} from "@ant-design/icons";
import { Modal, Tooltip } from "antd";
import { useState, useEffect } from "react";
import { https_taskflow } from "../../service/api";
import CommentSection from "../TaskComment/CommentSection";
import { LabelsSection } from "../Section/LabelsSection";
import PriorityDropdown from "../Dropdown/PriorityDropdown";
import { toast } from "sonner";
import dayjs from "dayjs";
import { CheckOutlined } from "@ant-design/icons";
import { DatePicker } from "antd";

export default function TaskDetailModal({
    isOpenComment,
    openTask,
    onClose,
    task,
    onUpdateStatus,
}) {
    const [taskDetail, setTaskDetail] = useState({});

    const formatDate = (date) => {
        if (!date) return "+";
        return dayjs(date).format("HH:mm:ss DD/MM/YYYY");
    };

    // Status color mapping
    const statusColors = {
        PENDING: "gray",
        READY: "blue",
        IN_PROGRESS: "orange",
        COMPLETED: "green",
        CANCELLED: "red",
    };

    const [projectDropdownOpen, setProjectDropdownOpen] = useState(false);
    const [projects, setProjects] = useState([]);
    const [loadingProjects, setLoadingProjects] = useState(false);
    const [currentTaskId, setCurrentTaskId] = useState(task?.id);

    const [editingTitle, setEditingTitle] = useState(false);
    const [titleDraft, setTitleDraft] = useState(taskDetail.title);

    const [editingDesc, setEditingDesc] = useState(false);
    const [descDraft, setDescDraft] = useState(taskDetail.description || "");

    const [openStartPicker, setOpenStartPicker] = useState(false);
    const [openDeadlinePicker, setOpenDeadlinePicker] = useState(false);

    const [memberDropdownOpen, setMemberDropdownOpen] = useState(false);
    const [members, setMembers] = useState([]);
    const [loadingMembers, setLoadingMembers] = useState(false);

    const [taskStack, setTaskStack] = useState([]);

    const openChildTask = (childTask) => {
        setTaskStack(prev => [...prev, taskDetail]);
        setCurrentTaskId(childTask.id); // 🔥 trigger API
    };



    const normalizeTask = (task) => ({
        ...task,
        taskChild: task.taskChild || [],
        comments: task.comments || [],
        labels: task.labels || []
    });


    const backToParentTask = () => {
        const prev = taskStack[taskStack.length - 1];
        if (!prev) return;

        setTaskStack(stack => stack.slice(0, -1));
        setCurrentTaskId(prev.id); // 🔥 trigger API again
    };


    const loadProjectMembers = async () => {
        try {
            setLoadingMembers(true);
            const res = await https_taskflow.get(
                `/v1/projects/${taskDetail.idProject}/members`
            );

            if (res.status === 200) {
                setMembers(res.data.data || []);
                setMemberDropdownOpen(true);
            }
        } catch (err) {
            toast.error("Failed to load project members");
        } finally {
            setLoadingMembers(false);
        }
    };






    const handleComment = async (newComment, attachments) => {
        try {
            const response = await https_taskflow.post(`/v1/projects/${task.idProject}/tasks/${task.id}/comments`, {
                comment: newComment,
                urls: attachments
            })

            if (response.status === 200) {
                const taskDetailNew = { ...taskDetail, comments: [...taskDetail.comments, response.data.data] };
                setTaskDetail(taskDetailNew);
            }


        } catch (err) {
            // Kiểm tra xem server có trả lỗi dạng JSON không
            if (err.response && err.response.data) {
                const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
                toast.error(msg);
            } else {
                toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
            }
        }
    };

    const onUpdateComment = async (newComment, idComment) => {
        if (!newComment.trim()) return;
        try {
            const res = await https_taskflow.patch(
                `/v1/projects/${task.idProject}/tasks/comments/${idComment}`,
                { comment: newComment }
            );

            if (res.status === 200) {
                const commentUpdated = res.data.data;
                const updatedComments = taskDetail.comments.map(comment =>
                    comment.id === commentUpdated.id ? commentUpdated : comment
                );
                setTaskDetail(prev => ({ ...prev, comments: updatedComments }));
            }
        } catch (err) {
            // Kiểm tra xem server có trả lỗi dạng JSON không
            if (err.response && err.response.data) {
                const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
                toast.error(msg);
            } else {
                toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
            }
        }
    };

    const onDeleteComment = async (idComment) => {
        try {
            const res = await https_taskflow.delete(
                `/v1/projects/${task.idProject}/tasks/comments/${idComment}`
            );

            if (res.status === 200) {
                const commentDeleted = res.data.data;
                const updatedComments = (taskDetail.comments || []).filter(
                    comment => comment.id !== commentDeleted.id
                );
                setTaskDetail(prev => ({ ...prev, comments: updatedComments }));
            }
        } catch (err) {
            // Kiểm tra xem server có trả lỗi dạng JSON không
            if (err.response && err.response.data) {
                const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
                toast.error(msg);
            } else {
                toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
            }
        }
    }

    const onDeleteCommentAttach = async (url) => {
        try {
            const res = await https_taskflow.delete(
                `/v1/projects/${task.idProject}/deleteCommentAttach`, {
                params: {
                    fileUrl: url
                }
            }
            );

            if (res.status === 200) {
                const commentAttachDeleted = res.data.data;
                const updatedComments = (taskDetail.comments || []).map(comment => {
                    // nếu đây là comment chứa attachment vừa xóa
                    if (comment.id === commentAttachDeleted.taskCommentId) {
                        return {
                            ...comment,
                            commentAttach: (comment.commentAttach || []).filter(
                                att => att.id !== commentAttachDeleted.id
                            )
                        };
                    }
                    return comment;
                });
                setTaskDetail(prev => ({ ...prev, comments: updatedComments }));
            }
        } catch (err) {
            // Kiểm tra xem server có trả lỗi dạng JSON không
            if (err.response && err.response.data) {
                const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
                toast.error(msg);
            } else {
                toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
            }
        }
    }

    const onUpdatePriority = async (newPriority) => {
        try {
            const res = await https_taskflow.patch(
                `/v1/projects/${task.idProject}/tasks/${task.id}/update-priority`, {
                priority: newPriority
            }
            );

            if (res.status === 200) {
                setTaskDetail(prev => ({ ...prev, priority: newPriority }));
            }
        } catch (err) {
            // Kiểm tra xem server có trả lỗi dạng JSON không
            if (err.response && err.response.data) {
                const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
                toast.error(msg);
            } else {
                toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
            }
        }
    }

    const updateTaskSection = async (projectId, sectionId) => {
        try {
            const res = await https_taskflow.patch(
                `/v1/projects/${projectId}/tasks/${task.id}/update-section`,
                {
                    idSection: sectionId
                }
            );

            if (res.status === 200) {
                const { projectName, sectionName } =
                    resolveProjectSectionName(projects, projectId, sectionId);

                setTaskDetail(prev => ({
                    ...prev,
                    idProject: projectId,
                    idSection: sectionId,
                    projectName,
                    sectionName
                }));

                toast.success("Task moved successfully");
                setProjectDropdownOpen(false);
            }

        } catch (err) {
            toast.error("Failed to update task section");
        }
    };


    const onClickProject = async (idProject, idSection) => {
        if (projectDropdownOpen) {
            setProjectDropdownOpen(false);
            return;
        }

        try {
            setLoadingProjects(true);

            const res = await https_taskflow.get(`/v1/projects`);

            if (res.status === 200) {
                const projectsData = res.data.data || [];
                setProjects(projectsData);

                const { projectName, sectionName } =
                    resolveProjectSectionName(
                        projectsData,
                        taskDetail.idProject,
                        taskDetail.idSection
                    );

                setTaskDetail(prev => ({
                    ...prev,
                    projectName,
                    sectionName
                }));

                setProjectDropdownOpen(true);
            }

        } catch (err) {
            toast.error("Failed to load projects");
        } finally {
            setLoadingProjects(false);
        }
    };

    useEffect(() => {
        if (!currentTaskId) return;

        const getDetails = async () => {
            try {
                const response = await https_taskflow.get(
                    `/v1/projects/${task.idProject}/tasks/${currentTaskId}`
                );

                setTaskDetail(normalizeTask(response.data.data));
            } catch (error) {
                console.log(error);
                toast.error("Failed to load task detail");
            }
        };

        getDetails();
    }, [currentTaskId]);



    const resolveProjectSectionName = (projects, idProject, idSection) => {
        const project = projects.find(p => p.id === idProject);
        const section = project?.section?.find(s => s.id === idSection);

        return {
            projectName: project?.name || "Unknown Project",
            sectionName: section?.name || "Unknown Section"
        };
    };

    const patchTask = async (payload) => {
        try {
            const res = await https_taskflow.patch(
                `/v1/projects/${taskDetail.idProject}/tasks/${taskDetail.id}`,
                payload
            );
            return res.status === 200;
        } catch (err) {
            toast.error("Failed to update task");
            return false;
        }
    };

    useEffect(() => {
        if (taskDetail.title !== undefined) {
            setTitleDraft(taskDetail.title);
        }
        if (taskDetail.description !== undefined) {
            setDescDraft(taskDetail.description || "");
        }
    }, [taskDetail.title, taskDetail.description]);


    useEffect(() => {
        const getDetails = async () => {
            try {
                const response = await https_taskflow.get(
                    `/v1/projects/${task.idProject}/tasks/${task.id}`
                );
                setCurrentTaskId(task.id);

                setTaskDetail(normalizeTask(response.data.data)); // ✅ FIX
            } catch (error) {
                console.log(error);
            }
        };

        getDetails();
    }, [task]);



    const onUpdateStartTime = async (date) => {
        const ok = await patchTask({
            startTime: date   // LocalDateTime (ISO)
        });

        if (ok) {
            setTaskDetail(prev => ({ ...prev, startTime: date }));
        }
    };

    const onUpdateDeadline = async (date) => {
        const ok = await patchTask({
            deadline: date
        });

        if (ok) {
            setTaskDetail(prev => ({ ...prev, deadline: date }));
        }
    };

    const onUpdateAssignee = async (accountId) => {
        const ok = await patchTask({
            accountAssignId: accountId || null
        });

        if (ok) {
            const selected = members.find(m => m.id === accountId) || null;
            setTaskDetail(prev => ({
                ...prev,
                accountAssign: selected
            }));
            setMemberDropdownOpen(false);
        }
    };



    return (


        <Modal
            open={!!openTask}
            onCancel={onClose}
            footer={null}
            width={1000}
            high={800}
            centered
            styles={{ body: { padding: 15, borderRadius: 10 } }}
        >

            {taskStack.length > 0 && (
                <div
                    className="text-sm text-blue-600 cursor-pointer hover:underline mb-2"
                    onClick={backToParentTask}
                >
                    ← Back to parent task
                </div>
            )}

            {Object.keys(taskDetail).length > 0 && (
                <div className="flex">
                    {/* LEFT CONTENT */}
                    <div className="flex-1 p-6 border-r">
                        <div className="flex items-center gap-2 mb-3">
                            <input
                                checked={taskDetail.status === "COMPLETED"}
                                type="checkbox"
                                className="cursor-pointer accent-red-500 w-5 h-5 rounded-full"
                                onChange={async (e) => {
                                    e.stopPropagation();
                                    const updatedStatus =
                                        taskDetail.status !== "COMPLETED" ? "COMPLETED" : "PENDING";

                                    if (await onUpdateStatus(updatedStatus)) {
                                        setTaskDetail(prev => ({ ...prev, status: updatedStatus }));
                                    }
                                }}
                            />

                            {editingTitle ? (
                                <input
                                    autoFocus
                                    value={titleDraft}
                                    onChange={e => setTitleDraft(e.target.value)}
                                    onBlur={async () => {
                                        if (titleDraft !== taskDetail.title) {
                                            const ok = await patchTask({ title: titleDraft });
                                            if (ok) {
                                                setTaskDetail(prev => ({ ...prev, title: titleDraft }));
                                            }
                                        }
                                        setEditingTitle(false);
                                    }}
                                    className="text-lg font-semibold w-full outline-none border-b"
                                />
                            ) : (
                                <h3
                                    className="font-semibold text-gray-800 text-lg cursor-text"
                                    onClick={() => setEditingTitle(true)}
                                >
                                    {taskDetail.title}
                                </h3>
                            )}
                        </div>

                        {editingDesc ? (
                            <textarea
                                autoFocus
                                value={descDraft}
                                onChange={e => setDescDraft(e.target.value)}
                                onBlur={async () => {
                                    if (descDraft !== taskDetail.description) {
                                        const ok = await patchTask({ description: descDraft });
                                        if (ok) {
                                            setTaskDetail(prev => ({ ...prev, description: descDraft }));
                                        }
                                    }
                                    setEditingDesc(false);
                                }}
                                className="w-full text-gray-700 border rounded p-2 mb-5 resize-none"
                                rows={3}
                            />
                        ) : (
                            <p
                                className="text-gray-500 mb-5 cursor-text"
                                onClick={() => setEditingDesc(true)}
                            >
                                {taskDetail.description || "Add description"}
                            </p>
                        )}

                        {taskDetail?.taskChild?.length > 0 && (
                            <div className="mb-4">
                                <div className="text-sm font-semibold text-gray-600 mb-2">
                                    Subtasks
                                </div>

                                <div className="space-y-1">
                                    {taskDetail.taskChild.map(child => (
                                        <div
                                            key={child.id}
                                            className="px-3 py-2 rounded cursor-pointer hover:bg-gray-100
                               flex items-center justify-between"
                                            onClick={() => openChildTask(child)}
                                        >
                                            <span className="text-sm text-gray-800">
                                                ▸ {child.title}
                                            </span>

                                            <span
                                                className="text-xs font-medium"
                                                style={{ color: statusColors[child.status] }}
                                            >
                                                {child.status}
                                            </span>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        )}



                        {/* Comment Box */}
                        <CommentSection
                            isOpenComment={isOpenComment}
                            comments={taskDetail?.comments ?? []}
                            handleComment={handleComment}
                            onUpdateComment={onUpdateComment}
                            onDeleteComment={onDeleteComment}
                            onDeleteCommentAttach={onDeleteCommentAttach}
                        />
                    </div>

                    {/* RIGHT SIDEBAR */}
                    <div className="w-72 bg-[#fcfaf8] p-4 space-y-2 border-l">
                        <SidebarItem
                            label="Project"
                            // icon={<LockOutlined />}
                            value={
                                taskDetail.projectName && taskDetail.sectionName
                                    ? `${taskDetail.projectName} / ${taskDetail.sectionName}`
                                    : "Select project"
                            }

                            onClick={() => onClickProject(taskDetail.idProject, taskDetail.idSection)}

                        />



                        {projectDropdownOpen && (
                            <div className="absolute right-0 mt-2 w-64 bg-white border rounded-lg shadow-lg z-50 max-h-96 overflow-auto">

                                {loadingProjects && (
                                    <div className="p-3 text-sm text-gray-500">
                                        Loading...
                                    </div>
                                )}

                                {!loadingProjects && projects.map(project => {
                                    const isCurrentProject = project.id === taskDetail.idProject;

                                    return (
                                        <div key={project.id} className="border-b last:border-b-0">

                                            {/* Project name */}
                                            <div
                                                className={`px-3 py-2 font-semibold flex items-center justify-between
                                                ${isCurrentProject ? "bg-blue-50 text-blue-700" : "bg-gray-50 text-gray-800"}`}
                                            >
                                                <span>{project.name}</span>
                                                {isCurrentProject && <CheckOutlined />}
                                            </div>

                                            {/* Sections */}
                                            {project.section?.map(section => {
                                                const isCurrentSection =
                                                    project.id === taskDetail.idProject &&
                                                    section.id === taskDetail.idSection;

                                                return (
                                                    <div
                                                        key={section.id}
                                                        className={`px-4 py-2 text-sm flex items-center justify-between cursor-pointer
                                                        ${isCurrentSection ? "bg-blue-100 text-blue-700" : "hover:bg-gray-100"}`}
                                                        onClick={() => {
                                                            if (isCurrentSection) return;

                                                            updateTaskSection(project.id, section.id);
                                                        }}
                                                    >
                                                        <span>▸ {section.name}</span>
                                                        {isCurrentSection && <CheckOutlined />}
                                                    </div>
                                                );
                                            })}

                                        </div>
                                    );
                                })}
                            </div>
                        )}

                        <SidebarItem label="Created by" disabled>
                            <div className="flex items-center gap-2">
                                <img
                                    src={
                                        taskDetail.createdByAccount?.avatar
                                        || "https://i.pravatar.cc/80"
                                    }
                                    alt="creator"
                                    className="w-6 h-6 rounded-full"
                                />
                                <span className="text-sm text-gray-700">
                                    {taskDetail.createdByAccount?.displayName || "Unknown"}
                                </span>
                            </div>
                        </SidebarItem>

                        <SidebarItem
                            label="Assignee"
                            onClick={() => {
                                if (memberDropdownOpen) {
                                    setMemberDropdownOpen(false);
                                } else {
                                    loadProjectMembers();
                                }
                            }}
                        >
                            <div className="flex items-center gap-2">
                                {taskDetail.accountAssign ? (
                                    <>
                                        <img
                                            src={
                                                taskDetail.accountAssign.avatar ||
                                                "https://i.pravatar.cc/80"
                                            }
                                            className="w-6 h-6 rounded-full"
                                        />
                                        <span className="text-sm text-gray-700">
                                            {taskDetail.accountAssign.displayName}
                                        </span>
                                    </>
                                ) : (
                                    <>
                                        {/* Placeholder avatar */}
                                        <div className="w-6 h-6 rounded-full border border-dashed border-red-400 flex items-center justify-center">
                                            <span className="text-xs text-red-400">?</span>
                                        </div>

                                        <span className="text-sm text-red-500 font-medium italic">
                                            Unassigned
                                        </span>
                                    </>
                                )}
                            </div>
                        </SidebarItem>


                        {memberDropdownOpen && (
                            <div className="absolute right-0 mt-2 w-64 bg-white border rounded-lg shadow-lg z-50 max-h-80 overflow-auto">
                                {loadingMembers && (
                                    <div className="p-3 text-sm text-gray-500">Loading...</div>
                                )}

                                {!loadingMembers && members.map(member => {
                                    const isAssigned =
                                        taskDetail.accountAssign?.id === member.id;

                                    return (
                                        <div
                                            key={member.id}
                                            className={`px-3 py-2 flex items-center gap-2 cursor-pointer
                    ${isAssigned ? "bg-blue-100 text-blue-700" : "hover:bg-gray-100"}`}
                                            onClick={() => {
                                                if (isAssigned) return;
                                                onUpdateAssignee(member.id);
                                            }}
                                        >
                                            <img
                                                src={member.avatar || "https://i.pravatar.cc/80"}
                                                className="w-6 h-6 rounded-full"
                                            />
                                            <span className="flex-1 text-sm">
                                                {member.displayName}
                                            </span>
                                            {isAssigned && <CheckOutlined />}
                                        </div>
                                    );
                                })}

                                {/* Unassign */}
                                <div
                                    className="px-3 py-2 text-sm text-red-500 cursor-pointer hover:bg-red-50"
                                    onClick={() => onUpdateAssignee(null)}
                                >
                                    Remove assignee
                                </div>
                            </div>
                        )}



                        {/* Date */}
                        <SidebarItem
                            label="Date"
                            onClick={() => setOpenStartPicker(true)}
                        >
                            {openStartPicker ? (
                                <div onClick={e => e.stopPropagation()}>
                                    <DatePicker
                                        open={openStartPicker}
                                        showTime
                                        autoFocus
                                        value={taskDetail.startTime ? dayjs(taskDetail.startTime) : null}
                                        onChange={async (value) => {
                                            if (!value) return;
                                            const iso = value.toISOString();
                                            await onUpdateStartTime(iso);
                                            setOpenStartPicker(false); // ✅ đóng khi OK
                                        }}
                                        onOpenChange={(open) => {
                                            if (!open) setOpenStartPicker(false); // ✅ click outside
                                        }}
                                    />
                                </div>
                            ) : (
                                <span>
                                    {taskDetail.startTime ? formatDate(taskDetail.startTime) : "+"}
                                </span>
                            )}
                        </SidebarItem>






                        {/* Deadline */}
                        <SidebarItem
                            label="Deadline"
                            onClick={() => setOpenDeadlinePicker(true)}
                        >
                            {openDeadlinePicker ? (
                                <div onClick={e => e.stopPropagation()}>
                                    <DatePicker
                                        open={openDeadlinePicker}
                                        showTime
                                        autoFocus
                                        value={taskDetail.deadline ? dayjs(taskDetail.deadline) : null}
                                        onChange={async (value) => {
                                            if (!value) return;
                                            const iso = value.toISOString();
                                            await onUpdateDeadline(iso);
                                            setOpenDeadlinePicker(false); // ✅ OK
                                        }}
                                        onOpenChange={(open) => {
                                            if (!open) setOpenDeadlinePicker(false); // ✅ click ngoài
                                        }}
                                    />
                                </div>
                            ) : (
                                <span>
                                    {taskDetail.deadline ? formatDate(taskDetail.deadline) : "+"}
                                </span>
                            )}
                        </SidebarItem>






                        <SidebarItem
                            label="Status"
                            value={
                                <span style={{ color: statusColors[taskDetail.status] || "black" }}>
                                    {taskDetail.status || "No status"}
                                </span>
                            }
                            onClick={() => console.log("Status clicked")}
                        />

                        <SidebarItem
                            label="Created"
                            value={formatDate(taskDetail.createdAt)}
                        />

                        <SidebarItem
                            label="Last updated"
                            value={formatDate(taskDetail.updatedAt)}
                        />



                        {/* Priority */}
                        <SidebarItem
                            label="Priority"
                        >
                            <PriorityDropdown priority={taskDetail.priority} onSelect={onUpdatePriority} />
                        </SidebarItem>





                        {/* Labels with dropdown */}
                        <LabelsSection taskDetail={taskDetail}></LabelsSection>

                        {/*/!* Reminders *!/*/}
                        {/*<SidebarItem*/}
                        {/*    label="Reminders"*/}
                        {/*    value="+"*/}
                        {/*    onClick={() => console.log("Reminders")}*/}
                        {/*/>*/}

                        {/*/!* Location *!/*/}
                        {/*<SidebarItem*/}
                        {/*    label="Location"*/}
                        {/*    icon={<LockOutlined />}*/}
                        {/*    onClick={() => console.log("Location locked")}*/}
                        {/*/>*/}
                    </div>
                </div>
            )}
        </Modal>
    );
}


export function SidebarItem({ label, value, icon, onClick, children, disabled }) {
    return (
        <>
            <div className="border-t border-gray-200 my-1" />

            <div
                className={`flex justify-between items-center px-3 py-2 rounded-lg
                ${disabled ? "" : "cursor-pointer hover:bg-gray-100"}`}
                onClick={disabled ? undefined : onClick}
            >
                <div className="flex items-center gap-2 text-gray-700">
                    {icon}
                    <span>{label}</span>
                </div>

                <div className="text-gray-600">
                    {value ?? children}
                </div>
            </div>
        </>
    );
}


