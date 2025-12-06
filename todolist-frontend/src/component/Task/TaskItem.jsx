import React, { useState, useRef, useEffect } from "react";
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
import { toast } from "sonner";

export default function TaskItem({
  onDeleteTask,
  sectionId,
  projectId,
  handleUpdateTask,
  task,
  onUpdate,
}) {
  const [isEditing, setIsEditing] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null);
  const [checked, setChecked] = useState(task.status === "COMPLETED");

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
  // const handleDelete = async () => {
  //   if (!window.confirm("Bạn có chắc muốn xoá task này?")) return;

  //   try {
  //     await https_taskflow.delete(`/v1/projects/${projectId}/tasks/${task.id}`);
  //     console.log("Deleted successfully");
  //   } catch (error) {
  //     console.error("Error deleting task:", error);
  //   }
  // };
  const handleToggleStatus = async () => {
    const newStatus = checked ? "PENDING" : "COMPLETED";
    setChecked(!checked);

    try {
      await https_taskflow.patch(
        `/v1/projects/${projectId}/tasks/${task.id}/update-status`,
        { status: newStatus }
      );

      // Cập nhật tại parent
      handleUpdateTask?.(sectionId, { ...task, status: newStatus });

      toast.success("Công việc đã hoàn thành!");
    } catch (error) {
      console.error("Update status failed:", error);
      toast.error("Cập nhật trạng thái thất bại!");

      // revert UI nếu lỗi
      setChecked(checked);
    }
  };

  const handleDelete = async () => {
    try {
      await https_taskflow.delete(`/v1/projects/${projectId}/tasks/${task.id}`);

      // ✅ cập nhật UI không cần reload
      onDeleteTask(sectionId, task.id);

      toast.error("Xoá task thành công!");
    } catch (error) {
      console.error("Error deleting task:", error);
      toast.error("Xoá task thất bại!");
    }
  };

  const handleSave = (updatedTask) => {
    onUpdate?.(updatedTask);
    setIsEditing(false);
  };
  const handleUpdateTaskAPI = async (updatedTask) => {
    console.log("updatedTask: ", updatedTask);
    try {
      const res = await https_taskflow.patch(
        `/v1/projects/${projectId}/tasks/${task.id}`,
        {
          title: updatedTask.title,
          description: updatedTask.description,
          priority: updatedTask.priority,
          isPinned: updatedTask.isPinned ?? task.isPinned ?? false,

          startTime: updatedTask.startTime || null,
          deadline: updatedTask.deadline || null,
          startTimeSent: !!updatedTask.startTime,
          deadlineSent: !!updatedTask.deadline,
        }
      );
      toast.error("Update thành công");
      // ✅ notify parent to update UI
      handleUpdateTask?.(sectionId, { ...task, ...updatedTask });

      setIsEditing(false);
    } catch (err) {
      console.error("❌ Update task failed:", err);
    }
  };

  if (isEditing) {
    return (
      <TaskEditForm
        onSave={(data) => handleUpdateTaskAPI(data)}
        task={task}
        onCancel={() => setIsEditing(false)}
      />
    );
  }

  return (
    <div className="group relative flex flex-col border-b hover:bg-gray-50 transition-colors px-2 py-2 rounded-md">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <GripVertical size={16} className="text-gray-400 cursor-grab" />
          <input
            type="checkbox"
            checked={checked}
            onChange={handleToggleStatus}
            className="cursor-pointer accent-red-500"
          />

          <span className="text-sm text-gray-800 font-medium">
            {task.title}
          </span>
        </div>

        <div className="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
          <button
            className="p-1 hover:text-gray-900 text-gray-500"
            onClick={() => setIsEditing(true)}
          >
            <Edit2 size={14} />
          </button>
          <button className="p-1 hover:text-gray-900 text-gray-500">
            <CalendarDays size={14} />
          </button>
          <button className="p-1 hover:text-gray-900 text-gray-500">
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
              {new Date(task.deadline).toLocaleDateString()}
            </span>
          )}
        </div>
      )}
    </div>
  );
}
