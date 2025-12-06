import { useState } from "react";
import { Button, Input, Typography } from "antd";
import TodayHeader from "../../component/Header/TodayHeader";
import MainLayout from "../../layout/MainLayout";

const { Title } = Typography;

export default function TodayPage() {
  const [showAddTask, setShowAddTask] = useState(false);
  const [taskName, setTaskName] = useState("");

  const handleAddTask = () => {
    if (!taskName.trim()) return;
    console.log("Task added:", taskName);
    setTaskName("");
    setShowAddTask(false);
  };

  return (
    <MainLayout>
      <div className="min-h-screen bg-white flex flex-col">
        {/* Header */}
        <TodayHeader />

        <div className="flex flex-col items-center justify-center flex-1 text-center mt-12 px-4">
          {/* Khi nhấn Add task thì hiện form */}
          {showAddTask ? (
            <div className="max-w-xl mx-auto w-full border rounded-xl shadow-sm p-5 bg-white text-left">
              <Title level={4} className="mb-3">
                Today
              </Title>

              <Input
                value={taskName}
                onChange={(e) => setTaskName(e.target.value)}
                placeholder="Go to the market Saturday morning"
                className="mb-2 py-2"
              />
              <p className="text-gray-500 text-xs mb-3">Description</p>

              {/* Các nút tùy chọn */}
              <div className="flex items-center gap-2 mb-4">
                <Button size="small" type="default">
                  📅 Today
                </Button>
                <Button size="small">⚑ Priority</Button>
                <Button size="small">⏰ Reminders</Button>
                <Button size="small">⋯</Button>
              </div>

              {/* Footer */}
              <div className="flex justify-between items-center">
                <Button type="text" icon={<i className="far fa-inbox"></i>}>
                  Inbox
                </Button>
                <div className="flex gap-2">
                  <Button onClick={() => setShowAddTask(false)}>Cancel</Button>
                  <Button
                    type="primary"
                    danger
                    disabled={!taskName.trim()}
                    onClick={handleAddTask}
                  >
                    Add task
                  </Button>
                </div>
              </div>
            </div>
          ) : (
            // Empty state khi chưa bấm Add task
            <>
              <button
                className="flex items-center gap-2 text-[13px] text-red-500 hover:text-red-600 mb-10"
                onClick={() => setShowAddTask(true)}
              >
                <span className="text-lg leading-none">＋</span>
                <span>Add task</span>
              </button>

              <img
                src="https://todoist.b-cdn.net/assets/images/35d9656fd2e3d183.png"
                alt="All done"
                className="w-60 mb-5"
              />

              <div className="text-[13px] text-gray-700 leading-relaxed">
                <p>
                  You're all done for the week,{" "}
                  <span className="font-semibold">Duongbinhminh10032004!</span>
                </p>
                <p className="mt-2 text-gray-500 text-xs max-w-sm mx-auto">
                  Enjoy the rest of your day and don’t forget to share your
                  <span className="text-gray-700 font-medium">
                    {" "}
                    #TodoistZero{" "}
                  </span>
                  awesomeness ↓
                </p>
              </div>

              <p className="text-red-500 text-xs mt-2 cursor-pointer hover:underline">
                Share #TodoistZero
              </p>
            </>
          )}
        </div>
      </div>
    </MainLayout>
  );
}
