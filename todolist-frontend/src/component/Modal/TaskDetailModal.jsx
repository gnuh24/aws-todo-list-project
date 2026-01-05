import {
    LockOutlined
} from "@ant-design/icons";
import { Modal, Button } from "antd";
import { useState, useEffect } from "react";
import { https_taskflow } from "../../service/api";
import CommentSection from "../TaskComment/CommentSection";
import { LabelsSection } from "../Section/LabelsSection";
import PriorityDropdown from "../Dropdown/PriorityDropdown";
import { toast } from "sonner";
import dayjs from "dayjs";
import { DatePicker } from "antd";
import ProjectSectionDropdown from "../Dropdown/ProjectSectionDropdown";
import MemberDropdown from "../Dropdown/MemberDropdown";
import { EditOutlined } from "@ant-design/icons";
import DateHelper from "../../helpers/DateHelper";
import TaskHelper from "../../helpers/TaskHelper";
import AvatarCircle from "../Content/AvatarCircle";
import { PlusOutlined } from "@ant-design/icons";
import AddTaskModal from "./AddTaskModal"; // adjust path if needed

export default function TaskDetailModal({
    isOpenComment,
    openTask,
    onClose,
    task,
    onUpdateStatus,
}) {


    // ╔══════════════════════════════════════╗
    // ║             💾 Component State       ║
    // ╚══════════════════════════════════════╝
    const [taskDetail, setTaskDetail] = useState({});
    const [taskStack, setTaskStack] = useState([]);
    const [currentTaskId, setCurrentTaskId] = useState(task?.id);

    const [editingTitle, setEditingTitle] = useState(false);
    const [titleDraft, setTitleDraft] = useState(taskDetail.title);

    const [editingDesc, setEditingDesc] = useState(false);
    const [descDraft, setDescDraft] = useState(taskDetail.description || "");

    const [openStartPicker, setOpenStartPicker] = useState(false);
    const [openDeadlinePicker, setOpenDeadlinePicker] = useState(false);

    const [memberDropdownOpen, setMemberDropdownOpen] = useState(false);
    const [projectDropdownOpen, setProjectDropdownOpen] = useState(false);

    const [showAddChildModal, setShowAddChildModal] = useState(false);
    const [parentTask, setParentTask] = useState(null);


    // ╔══════════════════════════════════════╗
    // ║           🔄 Task Navigation         ║
    // ╚══════════════════════════════════════╝

    const openChildTask = (childTask) => {
        setTaskStack(prev => [...prev, taskDetail]);
        setCurrentTaskId(childTask.id); // 🔥 trigger API
    };

    const backToParentTask = () => {
        const prev = taskStack[taskStack.length - 1];
        if (!prev) return;

        setTaskStack(stack => stack.slice(0, -1));
        setCurrentTaskId(prev.id); // 🔥 trigger API again
    };


    // ╔══════════════════════════════════════╗
    // ║          🚀 Task Update Helpers      ║
    // ╚══════════════════════════════════════╝

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
    };

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

    const onUpdateTaskSection = async (projectId, projectName, sectionId, sectionName) => {
        try {

            const res = await https_taskflow.patch(
                `/v1/projects/${projectId}/tasks/${task.id}/update-section`,
                { idSection: sectionId }
            );

            if (res.status === 200) {
                setTaskDetail(prev => ({
                    ...prev,
                    idProject: projectId,
                    projectName: projectName,
                    idSection: sectionId,
                    sectionName: sectionName
                }));

                toast.success("Task moved successfully");
                setProjectDropdownOpen(false);
            }
        } catch {
            toast.error("Failed to update task section");
        }
    };

    const onUpdateAssignee = async (accountId) => {
        try {
            let res;

            if (accountId === null) {
                // 🔴 REMOVE assignee
                res = await https_taskflow.delete(
                    `/v1/projects/${taskDetail.idProject}/tasks/${taskDetail.id}/assignee`
                );
            } else {
                // 🟢 ASSIGN assignee
                res = await https_taskflow.patch(
                    `/v1/projects/${taskDetail.idProject}/tasks/${taskDetail.id}/assign`,
                    { idAccount: accountId }
                );
            }

            if (res.status === 200) {
                setTaskDetail(prev => ({
                    ...prev,
                    accountAssign: accountId === null
                        ? null
                        : res.data.data.accountAssign
                }));

                toast.success(
                    accountId === null
                        ? "Unassigned task successfully"
                        : "Assign task successfully"
                );

                setMemberDropdownOpen(false);
            }
        } catch (err) {
            toast.error(
                accountId === null
                    ? "Failed to unassign task"
                    : "Failed to assign task"
            );
        }
    };

    const handleAddTask = async (newTask) => {
        try {
            const res = await https_taskflow.post(
                `/v1/projects/${taskDetail.idProject}/tasks`,
                {
                    ...newTask,
                    sectionId: taskDetail.idSection, // ✅ inherit parent section
                }
            );

            if (res.status === 200 && res.data?.data) {
                const createdTask = res.data.data;

                // 🔥 Append child task immediately
                setTaskDetail(prev => ({
                    ...prev,
                    taskChild: [...(prev.taskChild || []), createdTask],
                }));

                toast.success("Subtask created successfully");
            }
        } catch (err) {
            toast.error("Failed to create subtask");
        } finally {
            setShowAddChildModal(false);
            setParentTask(null);
        }
    };




    // ╔══════════════════════════════════════╗
    // ║            🔗 React Hooks            ║
    // ╚══════════════════════════════════════╝

    useEffect(() => {
        if (!currentTaskId) return;

        const getDetails = async () => {
            try {
                const response = await https_taskflow.get(
                    `/v1/projects/${task.idProject}/tasks/${currentTaskId}`
                );

                // console.log(response.data.data);

                setTaskDetail(TaskHelper.normalizeTask(response.data.data));
            } catch (error) {
                console.log(error);
                toast.error("Failed to load task detail");
            }
        };

        getDetails();
    }, [currentTaskId]);



    useEffect(() => {
        if (taskDetail.title !== undefined) {
            setTitleDraft(taskDetail.title);
        }
        if (taskDetail.description !== undefined) {
            setDescDraft(taskDetail.description || "");
        }
    }, [taskDetail.title, taskDetail.description]);



    useEffect(() => {
        if (task?.id) {
            setCurrentTaskId(task.id);
            setTaskStack([]); // reset navigation stack when opening new task
        }
    }, [task?.id]);







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
                                                style={{ color: TaskHelper.statusColors[child.status] }}
                                            >
                                                {child.status}
                                            </span>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        )}

                        <Button
                            type="dashed"
                            icon={<PlusOutlined />}
                            onClick={() => {
                                setParentTask(taskDetail);
                                setShowAddChildModal(true);
                            }}
                        >
                            Add Children Task
                        </Button>

                        <AddTaskModal
                            open={showAddChildModal}
                            parentTask={parentTask}
                            onCancel={() => {
                                setShowAddChildModal(false);
                                setParentTask(null);
                            }}
                            onAdd={handleAddTask}
                        />



                        {/* Comment Box */}

                        <CommentSection
                            isOpenComment={isOpenComment}
                            comments={taskDetail?.comments ?? []}
                            setTaskDetail={setTaskDetail}
                            taskDetail={taskDetail}
                        />



                    </div>

                    {/* RIGHT SIDEBAR */}
                    <div className="w-72 bg-[#fcfaf8] p-4 space-y-2 border-l">
                        <SidebarItem
                            label="Project"
                            onClick={() => setProjectDropdownOpen(v => !v)}
                        >
                            <EditableValue>
                                <span className="text-sm">
                                    {taskDetail.projectName || "Unknown Project"} /{" "}
                                    {taskDetail.sectionName || "Unknown Section"}
                                </span>
                            </EditableValue>
                        </SidebarItem>



                        <ProjectSectionDropdown
                            open={projectDropdownOpen}
                            onClose={() => setProjectDropdownOpen(false)}
                            taskDetail={taskDetail}
                            onUpdateSection={onUpdateTaskSection}
                        />

                        <SidebarItem
                            label={
                                <div className="flex items-center gap-1">
                                    <LockOutlined />
                                    <span>Created by</span>
                                </div>
                            }
                            disabled
                        >
                            <div className="flex items-center gap-2">
                                <AvatarCircle
                                    avatar={taskDetail.createdByAccount?.avatar}
                                    name={taskDetail.createdByAccount?.displayName}
                                    size={24}
                                />
                                <span className="text-sm text-gray-700">
                                    {taskDetail.createdByAccount?.displayName || "Unknown"}
                                </span>
                            </div>

                        </SidebarItem>


                        <SidebarItem
                            label="Assignee"
                            onClick={() => setMemberDropdownOpen(v => !v)}
                        >
                            <EditableValue>
                                <div className="flex items-center gap-2">
                                    {taskDetail.accountAssign ? (
                                        <>
                                            <AvatarCircle
                                                avatar={taskDetail.accountAssign.avatar}
                                                name={taskDetail.accountAssign.displayName}
                                                size={24}
                                            />
                                            <span className="text-sm">
                                                {taskDetail.accountAssign.displayName}
                                            </span>
                                        </>
                                    ) : (
                                        <>
                                            {/* Red "?" placeholder */}
                                            <div className="w-6 h-6 rounded-full bg-red-100 text-red-500 
                                    flex items-center justify-center text-sm font-semibold">
                                                ?
                                            </div>
                                            <span className="text-sm text-red-500 italic">
                                                Unassigned
                                            </span>
                                        </>
                                    )}
                                </div>
                            </EditableValue>
                        </SidebarItem>



                        <MemberDropdown
                            open={memberDropdownOpen}
                            taskDetail={taskDetail}
                            onAssign={onUpdateAssignee}
                            onClose={() => setMemberDropdownOpen(false)}
                        />





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
                                            await onUpdateStartTime(value.toISOString());
                                            setOpenStartPicker(false);
                                        }}
                                        onOpenChange={(open) => {
                                            if (!open) setOpenStartPicker(false);
                                        }}
                                    />
                                </div>
                            ) : (
                                <EditableValue>
                                    <span
                                        className={`text-sm ${taskDetail.startTime
                                            ? "text-gray-800"
                                            : "text-gray-400 italic"
                                            }`}
                                    >
                                        {taskDetail.startTime
                                            ? DateHelper.formatDate(taskDetail.startTime)
                                            : "Set start date"}
                                    </span>
                                </EditableValue>
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
                                            await onUpdateDeadline(value.toISOString());
                                            setOpenDeadlinePicker(false);
                                        }}
                                        onOpenChange={(open) => !open && setOpenDeadlinePicker(false)}
                                    />
                                </div>
                            ) : (
                                <EditableValue>
                                    <span className="text-sm">
                                        {taskDetail.deadline ? DateHelper.formatDate(taskDetail.deadline) : "+"}
                                    </span>
                                </EditableValue>
                            )}
                        </SidebarItem>



                        <SidebarItem
                            label="Status"
                            value={
                                <span style={{ color: TaskHelper.statusColors[taskDetail.status] || "black" }}>
                                    {taskDetail.status || "No status"}
                                </span>
                            }
                            onClick={() => console.log("Status clicked")}
                        />

                        <SidebarItem
                            label={
                                <div className="flex items-center gap-1">
                                    <LockOutlined />
                                    <span>Created</span>
                                </div>
                            }
                            value={DateHelper.formatDate(taskDetail.createdAt)}
                            disabled
                        />

                        <SidebarItem
                            label={
                                <div className="flex items-center gap-1">
                                    <LockOutlined />
                                    <span>Last updated</span>
                                </div>
                            }
                            value={DateHelper.formatDate(taskDetail.updatedAt)}
                            disabled
                        />


                        {/* Priority */}
                        <SidebarItem
                            label="Priority"
                        >
                            <PriorityDropdown priority={taskDetail.priority} onSelect={onUpdatePriority} />
                        </SidebarItem>





                        {/* Labels with dropdown */}
                        <LabelsSection taskDetail={taskDetail}></LabelsSection>

                    </div>
                </div>
            )
            }
        </Modal >
    );
}

const EditableValue = ({ children }) => (
    <div className="
        flex items-center gap-1
        cursor-pointer
        hover:bg-gray-100
        rounded
        px-1
        transition
        group
    ">
        {children}
        <EditOutlined
            className="
                text-gray-400
                opacity-0
                group-hover:opacity-100
                text-xs
            "
        />
    </div>
);


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


