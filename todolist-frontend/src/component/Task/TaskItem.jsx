// ✅ TaskItem.jsx
import React, { useState } from "react";
import {
  Edit2,
  CalendarDays,
  MessageSquare,
  MoreHorizontal,
  GripVertical,
} from "lucide-react";
import TaskEditForm from "../Task/TaskEditForm";

export default function TaskItem({ task, onUpdate }) {
  const [isEditing, setIsEditing] = useState(false);

  const handleSave = (updatedTask) => {
    onUpdate?.(updatedTask);
    setIsEditing(false);
  };

  if (isEditing) {
    return (
      <TaskEditForm
        task={task}
        onSave={handleSave}
        onCancel={() => setIsEditing(false)}
      />
    );
  }

  return (
    <div className="group flex flex-col border-b hover:bg-gray-50 transition-colors px-2 py-2 rounded-md">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <GripVertical size={16} className="text-gray-400 cursor-grab" />
          <input type="checkbox" className="cursor-pointer accent-red-500" />
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
          <button className="p-1 hover:text-gray-900 text-gray-500">
            <MoreHorizontal size={14} />
          </button>
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
