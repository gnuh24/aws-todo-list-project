import { useState } from "react";
import Sidebar from "./Sidebar";
import TaskHeader from "./TaskHeader";
import TaskEmptyState from "./TaskEmptyState";
import TaskList from "./TaskList";

export default function TaskPage() {
  const [tasks, setTasks] = useState([
    {
      id: 1,
      title: "Watch: From other task apps to Todoist 🎬",
      description: "Click here to open the task view and access the video!",
      completed: false,
      deadline: "2025-09-29",
      project: "Getting Started 👋",
      commentCount: 1,
    },
    {
      id: 2,
      title: "Set aside 5 minutes to review your Inbox",
      description:
        "Use this time to organize, prioritize, schedule or delete tasks.",
      completed: false,
      deadline: "2025-09-29",
      project: "Getting Started 👋",
      commentCount: 1,
    },
    {
      id: 3,
      title: "Do coding practice",
      completed: false,
      deadline: "2025-10-01",
      project: "Work",
      commentCount: 0,
    },
  ]);

  const toggleTask = (id) => {
    setTasks((prev) =>
      prev.map((t) => (t.id === id ? { ...t, completed: !t.completed } : t))
    );
  };

  const deleteTask = (id) => {
    setTasks((prev) => prev.filter((t) => t.id !== id));
  };

  const editTask = (id) => {
    alert("Open edit modal for task id: " + id);
  };

  return (
    <div className="flex">
      <Sidebar />
      <div className="flex-1 bg-gray-50">
        <TaskHeader />
        {tasks.length > 0 ? (
          <TaskList
            tasks={tasks}
            onToggle={toggleTask}
            onDelete={deleteTask}
            onEdit={editTask}
          />
        ) : (
          <TaskEmptyState />
        )}
      </div>
    </div>
  );
}
