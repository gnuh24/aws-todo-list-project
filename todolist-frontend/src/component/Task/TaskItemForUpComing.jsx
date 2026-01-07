import React, { useState, useRef, useEffect } from "react";
import { Dropdown } from "antd";

import {
    Edit2,
    CalendarDays,
    MessageSquare,
    MoreHorizontal,
    GripVertical,
    Trash2,
    Flag,
} from "lucide-react";
import { https_taskflow } from "../../service/api";
import dayjs from "dayjs";
import {toast} from "sonner";
import TaskEditFormUpComing from "./TaskEditFormUpComing";
import DatePickerDropdownForUpComing from "../Dropdown/DatePickerDropdownForUpComing";
import {useProjectContext} from "../../context/ProjectContext";
import {useUIContext} from "../../context/UIContext";

export default function TaskItemForUpComing({
                                                projectId,
                                                task,
                                                onDeleteTaskUpComing,
                                                onUpdateTaskUpComing,
                                            }) {

    const { setActiveTaskId } = useProjectContext();

    const {
        setIsOpenComment
    } = useUIContext();

    const [isEditing, setIsEditing] = useState(false);
    const [newStatus, setNewStatus] = useState(task.status);
    const [menuOpen, setMenuOpen] = useState(false);
    const menuRef = useRef(null);
    const formatToDisplay = "HH:mm DD/MM/YYYY";
    const formatToSend = "YYYY-MM-DDTHH:mm:ss";
    const [showFormDatePicker, setShowFormDatePicker] = useState(false);


    // Đóng menu khi click ra ngoài
    useEffect(() => {
        const handler = (e) => {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
                setMenuOpen(false);
            }
        };
        document.addEventListener("mousedown", handler);
        return () => document.removeEventListener("mousedown", handler);
    }, []);

    const handleDelete = async () => {
        const confirmed = window.confirm("Bạn có chắc chắn muốn xóa task này không?");
        if (!confirmed) {
            return;
        }

        try {
            const response = await https_taskflow.delete(`/v1/projects/${projectId}/tasks/${task.id}`);


            // Dùng cho việc xóa task trong phần upcoming
            onDeleteTaskUpComing?.(response.data.data);

            toast.success("Xoá task thành công!");
        } catch (error) {
            toast.error(error?.response?.data?.message || "Xóa thất bại, vui lòng thử lại!");
        }
    };


    const handleUpdateTaskAPI = async (updatedTask) => {
        try {
            const res = await https_taskflow.patch(
                `/v1/projects/${projectId}/tasks/${task.id}`,
                {
                    title: updatedTask.title,
                    description: updatedTask.description,
                    priority: updatedTask.priority,
                    isPinned: updatedTask.isPinned ?? task.isPinned ?? false,
                    idSection: updatedTask.idSection,
                    startTime: updatedTask.startTime || null,
                    deadline: updatedTask.deadline || null,
                    startTimeSent: true,
                    deadlineSent: true,
                }
            );
            toast.success("Update thành công");

            // Nếu là cập nhật taskUpComing thì reload lại list
            onUpdateTaskUpComing?.(res.data.data);

            setIsEditing(false);
        } catch (err) {
            toast.error(err?.response?.data?.message || "Cập nhật thất bại, vui lòng thử lại!");
        }
    };

    const handleUpdateStatus = async (updatedStatus) => {
        try {
            const res = await https_taskflow.patch(
                `/v1/projects/${projectId}/tasks/${task.id}/update-status`,
                {
                    status: updatedStatus,
                }
            );

            if (res.status === 200) {
                const updatedTask = res.data.data;

                // Nếu là cập nhật taskUpComing thì reload lại list
                onUpdateTaskUpComing?.(updatedTask);

                setNewStatus(updatedTask.status);

                return true;
            } else {
                setNewStatus(task.status);

                return false;
            }

        } catch (err) {
            toast.error(err?.response?.data?.message || "Cập nhật thất bại, vui lòng thử lại!")
            setNewStatus(task.status);
        }
    }

    const handleUpdatePriority = async (priority) => {
        try {
            const res = await https_taskflow.patch(
                `/v1/projects/${projectId}/tasks/${task.id}/update-priority`,
                {
                    priority: priority,
                }
            );

            if (res.status === 200) {
                console.log(res.data);
                const updatedTask = res.data.data;

                // Nếu là cập nhật taskUpComing thì reload lại list
                onUpdateTaskUpComing?.(updatedTask);

                return true;
            }

        } catch (err) {
            toast.error(err?.response?.data?.message || "Cập nhật thất bại, vui lòng thử lại!")
            setNewStatus(task.status);
        }
    }

    // Nếu có gửi isOpenFormAddTaskUpComing thì phải null mới cho chạy
    if (isEditing) {
        return (
            <TaskEditFormUpComing
                onSave={(data) => handleUpdateTaskAPI(data)}
                task={task}
                onCancel={(e) => {
                    setIsEditing(false)
                }}
            />
        );
    }

    return (
        <div className="group relative flex flex-col border-b hover:bg-gray-50 transition-colors px-2 py-2 rounded-md"
             onClick={(e) => {
                 e.stopPropagation();
                 setActiveTaskId(task.id);
             }}>
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <GripVertical size={16} className="text-gray-400 cursor-grab" />
                    <input
                        checked={newStatus === "COMPLETED"}
                        type="checkbox"
                        className="rounded-full cursor-pointer accent-red-500 w-4 h-4l"
                        onClick={(e) => e.stopPropagation()}
                        onChange={() => {
                            setNewStatus(prev => {
                                const updatedStatus = prev !== "COMPLETED" ? "COMPLETED" : "PENDING"
                                handleUpdateStatus(updatedStatus);
                            });
                        }}
                    />
                    <span className="text-sm text-gray-800 font-medium">
            {task.title}
          </span>
                </div>

                <div className="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                    <button
                        className="p-1 hover:text-gray-900 text-gray-500"
                        onClick={(e) => {
                            e.stopPropagation();
                            setIsEditing(true)
                        }}
                    >
                        <Edit2 size={14} />
                    </button>

                    <Dropdown
                        trigger={["click"]}
                        open={showFormDatePicker}
                        onClick={(e)=>e.stopPropagation()}
                        onOpenChange={(v) => setShowFormDatePicker(v)}
                        dropdownRender={() => (
                            <div onClick={(e) => e.stopPropagation()}>
                                <DatePickerDropdownForUpComing
                                    isStartTime={true}
                                    onSelect={(newStartTime) => {
                                        const taskUpdate = { ...task, startTime: dayjs(newStartTime).format(formatToSend) };
                                        handleUpdateTaskAPI(taskUpdate);
                                    }}
                                    showForm={showFormDatePicker}
                                />
                            </div>
                        )}
                    >
                        <button
                            className="p-1 hover:text-gray-900 text-gray-500"
                            onClick={(e) => e.stopPropagation()} // <-- thêm dòng này
                        >
                            <CalendarDays size={14} />
                        </button>
                    </Dropdown>


                    <button className="p-1 hover:text-gray-900 text-gray-500" onClick={(e) => {
                        e.stopPropagation();
                        setActiveTaskId(task.id);
                        setIsOpenComment(true);
                    }}>
                        <MessageSquare size={14} />
                    </button>

                    {/* More menu button */}
                    <div className="relative" ref={menuRef}>
                        <button
                            className="p-1 hover:text-gray-900 text-gray-500"
                            onClick={(e) => {
                                e.stopPropagation();
                                setMenuOpen(!menuOpen)

                            }}
                        >
                            <MoreHorizontal size={14} />
                        </button>

                        {menuOpen && (
                            <div className="absolute right-0 mt-1 w-44 bg-white border rounded-md shadow-lg py-1 text-sm animate-fade-in z-50"
                                 onClick={(e)=>{e.stopPropagation()}}>
                                {/*<button className="flex items-center gap-2 w-full px-3 py-2 hover:bg-gray-100 text-gray-700">*/}
                                {/*    <ArrowUp size={14} /> Add task above*/}
                                {/*</button>*/}
                                {/*<button className="flex items-center gap-2 w-full px-3 py-2 hover:bg-gray-100 text-gray-700">*/}
                                {/*    <ArrowDown size={14} /> Add task below*/}
                                {/*</button>*/}
                                {/*<div className="flex items-center gap-2 w-full px-3 py-2 hover:bg-gray-100 text-gray-700">*/}
                                {/*    <Flag size={14} />*/}
                                {/*    <span>Priority</span>*/}
                                {/*    <PriorityDropdown priority={task.priority} onSelect={handleUpdatePriority}/>*/}
                                {/*</div>*/}
                                {/*<button className="flex items-center gap-2 w-full px-3 py-2 hover:bg-gray-100 text-gray-700">*/}
                                {/*    <Copy size={14} /> Duplicate*/}
                                {/*</button>*/}
                                <button
                                    onClick={(e)=>{
                                        e.stopPropagation()
                                        handleDelete()
                                    }}
                                    className="flex items-center gap-2 w-full px-3 py-2 hover:bg-gray-100 text-red-600"
                                >
                                    <Trash2 size={14} /> Delete
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {(task.description || task.deadline) && (
                <div className="pl-7 pr-2 mt-1 text-xs flex items-center gap-3">
                    {task.description && (
                        <span className="text-gray-500">{task.description}</span>
                    )}
                    {task.deadline && (
                        <span className="text-red-500 flex items-center gap-1">
              <CalendarDays size={12} />
                            {dayjs(task.deadline).format(formatToDisplay)}
            </span>
                    )}
                </div>
            )}
        </div>


    );
}