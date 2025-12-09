import React, { useState } from "react";

export default function AddTaskModalProject({ onClose, onAdd }) {
  const [taskName, setTaskName] = useState("");
  const [taskDesc, setTaskDesc] = useState("");

  const handleSubmit = () => {
    if (!taskName.trim()) return;
    onAdd({ title: taskName, desc: taskDesc, id: Date.now() });
    setTaskName("");
    setTaskDesc("");
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-xl shadow-lg w-[420px] relative">
        <h2 className="text-lg font-semibold mb-3">Add new task</h2>

        <div className="flex flex-col gap-3">
          <input
            type="text"
            placeholder="Task name"
            value={taskName}
            onChange={(e) => setTaskName(e.target.value)}
            className="border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-400"
          />
          <textarea
            placeholder="Description (optional)"
            value={taskDesc}
            onChange={(e) => setTaskDesc(e.target.value)}
            className="border rounded-lg px-3 py-2 h-24 resize-none focus:outline-none focus:ring-2 focus:ring-red-400"
          />
        </div>

        {/* Nút hành động */}
        <div className="flex justify-end gap-3 mt-5">
          <button
            onClick={onClose}
            className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600"
          >
            Add task
          </button>
        </div>

        {/* Nút đóng góc phải */}
        <button
          onClick={onClose}
          className="absolute top-3 right-4 text-gray-400 hover:text-gray-700"
        >
          ✖
        </button>
      </div>
    </div>
  );
}
