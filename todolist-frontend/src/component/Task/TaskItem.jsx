import React, { useState, useRef, useEffect } from "react";
import { Dropdown } from "antd";

import {
  Edit2,
  CalendarDays,
  MessageSquare,
  MoreHorizontal,
  GripVertical,
  Trash2,
  Copy,
  ArrowUp,
  ArrowDown,
  Flag,
} from "lucide-react";
import TaskEditForm from "../Task/TaskEditForm";
import { https_taskflow } from "../../service/api";
import dayjs from "dayjs";
import DatePickerDropdown from "../Dropdown/DatePickerDropdown";
import TaskDetailModal from "../Modal/TaskDetailModal";
import {toast} from "sonner";

export default function TaskItem({
  onDeleteTask,
  sectionId,
  projectId,
  task,
  onUpdate,
  onDeleteTaskUpComing,
  onUpdateTaskUpComing,
  isOpenFormAddTaskUpComing,
}) {
  const [isEditing, setIsEditing] = useState(false);
  const [isOpenComment, setIsOpenComment] = useState(false);
  const [openTaskDetailModal, setOpenTaskDetailModal] = useState(false);
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

      // ✅ cập nhật UI không cần reload
      onDeleteTask?.(sectionId, task.id);

      // Dùng cho việc xóa task trong phần upcoming
      onDeleteTaskUpComing?.(response.data.data);

      toast.success("Xoá task thành công!");
    } catch (error) {
        toast.error(error?.response?.data?.message || "Xóa thất bại, vui lòng thử lại!");
    }
  };

  const handleSave = (updatedTask) => {
    onUpdate?.(updatedTask);
    setIsEditing(false);
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
      // ✅ notify parent to update UI
      onUpdate?.(sectionId, { ...task, ...updatedTask });

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

        onUpdate?.(sectionId, { ...task, ...updatedTask });

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

  // Nếu có gửi isOpenFormAddTaskUpComing thì phải null mới cho chạy
  if (isEditing && (typeof isOpenFormAddTaskUpComing === "undefined" || isOpenFormAddTaskUpComing === null)) {
    return (
      <TaskEditForm
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
           setIsOpenComment(false)
           setOpenTaskDetailModal(true)
         }}>
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <GripVertical size={16} className="text-gray-400 cursor-grab" />
          <input
              checked={newStatus === "COMPLETED"}
              type="checkbox"
              className="rounded-full cursor-pointer accent-red-500 w-4 h-4l"
              onChange={(e) => {
                e.stopPropagation();
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
              onOpenChange={(v) => {
                setShowFormDatePicker(v); // mỗi lần mở lại form
              }}
              dropdownRender={() => (
                  <DatePickerDropdown
                      isStartTime={true}
                      onSelect={(newStartTime) => {
                        const taskUpdate = { ...task, startTime: dayjs(newStartTime).format(formatToSend) };
                        handleUpdateTaskAPI(taskUpdate);
                      }}
                      showForm={showFormDatePicker}
                  />
              )}
          >
            <button className="p-1 hover:text-gray-900 text-gray-500">
              <CalendarDays size={14} />
            </button>
          </Dropdown>

          <button className="p-1 hover:text-gray-900 text-gray-500" onClick={(e) => {
            e.stopPropagation();
            setIsOpenComment(true)
            setOpenTaskDetailModal(true)
          }}>
            <MessageSquare size={14} />
          </button>

          {/* More menu button */}
          <div className="relative" ref={menuRef}>
            <button
              className="p-1 hover:text-gray-900 text-gray-500"
              onClick={() => setMenuOpen(!menuOpen)}
            >
              <MoreHorizontal size={14} />
            </button>

            {menuOpen && (
              <div className="absolute right-0 mt-1 w-44 bg-white border rounded-md shadow-lg py-1 text-sm animate-fade-in z-50">
                <button className="flex items-center gap-2 w-full px-3 py-2 hover:bg-gray-100 text-gray-700">
                  <ArrowUp size={14} /> Add task above
                </button>
                <button className="flex items-center gap-2 w-full px-3 py-2 hover:bg-gray-100 text-gray-700">
                  <ArrowDown size={14} /> Add task below
                </button>
                <button className="flex items-center gap-2 w-full px-3 py-2 hover:bg-gray-100 text-gray-700">
                  <Flag size={14} /> Priority
                </button>
                <button className="flex items-center gap-2 w-full px-3 py-2 hover:bg-gray-100 text-gray-700">
                  <Copy size={14} /> Duplicate
                </button>
                <button
                  onClick={handleDelete}
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

      <TaskDetailModal
          isOpenComment={isOpenComment}
          openTask={openTaskDetailModal}       // boolean
          task={task}                      // dữ liệu task
          onClose={(e) => {
            e.stopPropagation();
            setOpenTaskDetailModal(false)
            setIsOpenComment(false)
          }}   // hàm đóng
          onUpdateStatus={handleUpdateStatus}
      />
    </div>


  );
}
