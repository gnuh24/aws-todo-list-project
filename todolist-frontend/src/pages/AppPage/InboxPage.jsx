import { useEffect, useState } from "react";
import { Button, Typography, Input } from "antd";
import InboxHeader from "../../component/Header/InboxHeader";
import MainLayout from "../../layout/MainLayout";
import { PlusOutlined } from "@ant-design/icons";
import { useLocation } from "react-router-dom";
const { Title, Paragraph } = Typography;

export default function InboxPage() {
  const [showAddTask, setShowAddTask] = useState(false);
  const [taskName, setTaskName] = useState("");
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get("token");
    if (token) {
      localStorage.setItem("ACCESS_TOKEN", token);
      window.history.replaceState({}, document.title, "/app/inbox");
    }
  }, [location]);
  const handleAddTask = () => {
    if (!taskName.trim()) return;
    console.log("New task:", taskName);
    setTaskName("");
    setShowAddTask(false);
  };

  return (
    <MainLayout>
      <InboxHeader />
      <div className="flex-1 flex flex-col bg p-8">
        {/* Khi nhấn + Add task */}
        {showAddTask ? (
          <div className="max-w-xl mx-auto w-full border rounded-xl shadow-sm p-5 bg-white">
            <Title level={4} className="mb-3">
              Inbox
            </Title>
            <Input
              value={taskName}
              onChange={(e) => setTaskName(e.target.value)}
              placeholder="Send price list by Wed at 2pm"
              className="mb-2 py-2"
            />
            <p className="text-gray-500 text-xs mb-3">Description</p>

            {/* Các nút tùy chọn */}
            <div className="flex items-center gap-2 mb-4">
              <Button size="small">📅 Date</Button>
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
                  onClick={handleAddTask}
                  disabled={!taskName.trim()}
                >
                  Add task
                </Button>
              </div>
            </div>
          </div>
        ) : (
          // Empty State (khi chưa nhấn Add task)
          <div className="flex flex-col items-center justify-center mt-24 text-center">
            <img
              src="https://cdn-icons-png.flaticon.com/512/4072/4072353.png"
              alt="empty inbox"
              className="w-40 h-40 mb-4"
            />
            <Title level={5} className="font-medium">
              Capture now, plan later
            </Title>
            <Paragraph className="text-gray-500 max-w-sm text-sm leading-relaxed">
              Inbox is your go-to spot for quick task entry. Clear your mind
              now, organize when you’re ready.
            </Paragraph>
            <Button
              type="primary"
              danger
              className="mt-3 rounded-md"
              icon={<PlusOutlined />}
              onClick={() => setShowAddTask(true)}
            >
              Add task
            </Button>
          </div>
        )}
      </div>
    </MainLayout>
  );
}
