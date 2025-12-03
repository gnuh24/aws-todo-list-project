import React, { useState } from "react";
import TaskItem from "../Task/TaskItem";

export default function SectionItem({
  handleDeleteTask,
  handleUpdateTask,
  projectId,
  section,
  onAddTaskClick,
}) {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <div className="border rounded-md p-3">
      {/* Header section */}
      <div
        className="flex justify-between items-center cursor-pointer"
        onClick={() => setCollapsed(!collapsed)}
      >
        <h3 className="font-semibold text-gray-800 flex items-center gap-1">
          <span>{collapsed ? "▶" : "▼"}</span>
          {section.name}
        </h3>
        <button className="hover:text-gray-600 text-gray-400">⋯</button>
      </div>

      {/* Task list */}
      {!collapsed && (
        <div className="mt-2">
          {section.tasks?.length ? (
            section.tasks.map((task) => (
              <TaskItem
                sectionId={section.id}
                onDeleteTask={handleDeleteTask}
                projectId={projectId}
                key={task.id}
                task={task}
              />
            ))
          ) : (
            <p className="text-sm text-gray-400 italic mt-1">No tasks yet</p>
          )}

          {/* Nút Add task */}
          <div
            className="text-red-500 text-sm mt-2 cursor-pointer hover:text-red-600 flex items-center gap-1"
            onClick={() => onAddTaskClick(section.id)} // ✅ Gọi callback từ cha
          >
            <span className="text-lg">＋</span>
            Add task
          </div>
        </div>
      )}
    </div>
  );
}
