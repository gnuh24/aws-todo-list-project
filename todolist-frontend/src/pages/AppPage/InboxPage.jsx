import { useEffect, useState } from "react";
import { Button, Typography, Input } from "antd";
import InboxHeader from "../../component/Header/InboxHeader";
import { PlusOutlined } from "@ant-design/icons";
import { useSearchParams, useNavigate } from "react-router-dom";
import { toast } from "sonner";


const { Title, Paragraph } = Typography;

export default function InboxPage() {
  const [showAddTask, setShowAddTask] = useState(false);
  const [taskName, setTaskName] = useState("");
  const [params] = useSearchParams();
  const navigate = useNavigate();

  // ============================
  // 🔥 XỬ LÝ GOOGLE LOGIN CALLBACK
  // ============================
  useEffect(() => {
    const id = params.get("id");
    const email = params.get("email");
    const displayName = params.get("displayName");
    const avatar = params.get("avatar");
    const role = params.get("role");
    const token = params.get("token");
    const refreshToken = params.get("refreshToken");

    if (!token) return; // Không phải callback OAuth → bỏ qua

    const userData = {
      id,
      email,
      displayName,
      avatar,
      role,
      token,
      refreshToken,
    };

    // Lưu vào localStorage
    localStorage.setItem("USER_INFO", JSON.stringify(userData));
    localStorage.setItem("accessToken", token);
    localStorage.setItem("refreshToken", refreshToken);

    toast.success("Đăng nhập thành công!");

    // Chuyển hướng sang /app/inbox (hoặc /home)
    setTimeout(() => {
      navigate("/app/inbox");
    }, 800);
  }, [params, navigate]);

  // ============================
  // ADD TASK
  // ============================
  const handleAddTask = () => {
    if (!taskName.trim()) return;

    console.log("New task:", taskName);

    setTaskName("");
    setShowAddTask(false);
  };

  return (
    <>
      <InboxHeader />

      <div className="flex-1 flex flex-col bg p-8">
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

            <div className="flex items-center gap-2 mb-4">
              <Button size="small">📅 Date</Button>
              <Button size="small">⚑ Priority</Button>
              <Button size="small">⏰ Reminders</Button>
              <Button size="small">⋯</Button>
            </div>

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
          // EMPTY STATE
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
    </>
  );
}
