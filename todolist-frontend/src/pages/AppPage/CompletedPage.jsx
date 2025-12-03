// src/pages/CompletedPage.jsx
import { useState } from "react";
import { CheckCircleFilled, LockOutlined } from "@ant-design/icons";
import ActivityHeader from "../../component/Header/ActivityHeader";
import MainLayout from "../../layout/MainLayout";
import TaskDetailModal from "../../component/Modal/TaskDetailModal";

const activityData = [
  { id: 1, task: "4m", desc: "123124124123", time: "22:17", project: "Inbox" },
  {
    id: 2,
    task: "quewq",
    desc: "Learn React",
    time: "21:41",
    project: "Inbox",
  },
  {
    id: 3,
    task: "go to school",
    desc: "Prepare homework",
    time: "18:44",
    project: "Inbox",
  },
];

export default function CompletedPage() {
  const [openTask, setOpenTask] = useState(null);

  const handleTaskClick = (task) => setOpenTask(task);
  const handleClose = () => setOpenTask(null);

  return (
    <MainLayout>
      <div className="px-10 py-6">
        <ActivityHeader />

        <div className="mb-5 font-semibold text-gray-800 text-sm">
          20 Oct · Yesterday · Monday
        </div>

        <div className="space-y-5">
          {activityData.map((item) => (
            <div
              key={item.id}
              className="flex items-start justify-between border-b pb-4 cursor-pointer hover:bg-gray-50"
              onClick={() => handleTaskClick(item)}
            >
              <div className="flex items-start gap-3">
                <div className="relative">
                  <img
                    src="https://i.pravatar.cc/40"
                    alt="user avatar"
                    className="rounded-full w-9 h-9"
                  />
                  <CheckCircleFilled className="text-green-500 absolute -bottom-1 -right-1 bg-white rounded-full text-lg" />
                </div>
                <div>
                  <p className="text-sm">
                    <span className="font-semibold">You</span> completed a task:{" "}
                    <span className="underline text-black hover:text-blue-600">
                      {item.task}
                    </span>
                  </p>
                  <p className="text-xs text-gray-400 mt-1">{item.time}</p>
                </div>
              </div>

              <div className="text-xs text-gray-500 flex items-center gap-1">
                {item.project}
                <LockOutlined className="text-[10px]" />
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Modal component */}
      <TaskDetailModal openTask={openTask} onClose={handleClose} />
    </MainLayout>
  );
}
