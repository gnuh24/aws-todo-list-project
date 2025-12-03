import React, { useState } from "react";
import MoreMenu from "../Dropdown/MoreMenu";
import TaskItem from "../Task/TaskItem";

export default function SectionItem({
  handleDeleteTask,
  handleUpdateTask,
  projectId,
  section,
  onAddTaskClick,
  handleSaveEdit,
  handleDeleteSection,
  handleArchive,
}) {
  const [collapsed, setCollapsed] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editName, setEditName] = useState(section.name);

  return (
    <div className="border rounded-md p-3">
      {/* Header section */}
      {isEditing ? (
        <div className="space-y-3 w-full">
          <input
            value={editName}
            onChange={(e) => setEditName(e.target.value)}
            className="w-full border rounded-lg px-3 py-2 outline-none focus:ring focus:ring-blue-100"
            autoFocus
          />

          <div className="flex items-center gap-4">
            <button
              className="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600"
              onClick={() =>
                handleSaveEdit(
                  section.id,
                  editName,
                  (projectId = { projectId })
                )
              }
            >
              Save
            </button>

            <button
              className="text-gray-500 hover:underline"
              onClick={() => {
                setEditName(section.name);
                setIsEditing(false);
              }}
            >
              Cancel
            </button>
          </div>
        </div>
      ) : (
        <div className="flex justify-between items-center">
          <h2 className="text-lg font-semibold">{section.name}</h2>

          <MoreMenu
            onEdit={() => setIsEditing(true)}
            onDelete={() => handleDeleteSection(section.id)}
            onArchive={() => handleArchive(section.id)}
          />
        </div>
      )}

      {/* Task list */}
      {!collapsed && (
        <div className="mt-2">
          {section.tasks?.filter((task) => task.status === "PENDING").length >
          0 ? (
            section.tasks
              .filter((task) => task.status === "PENDING")
              .map((task) => (
                <TaskItem
                  key={task.id}
                  task={task}
                  sectionId={section.id}
                  projectId={projectId}
                  onDeleteTask={handleDeleteTask}
                  handleUpdateTask={handleUpdateTask}
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
