// src/components/TaskDetailModal.jsx
import {
  CheckCircleFilled,
  LockOutlined,
  PaperClipOutlined,
  AudioOutlined,
  SmileOutlined,
  EnterOutlined,
} from "@ant-design/icons";
import { Modal, Input, Button } from "antd";
import { useState } from "react";

export default function TaskDetailModal({ openTask, onClose }) {
  const [comment, setComment] = useState("");

  const handleComment = () => {
    console.log("Comment:", comment);
    setComment("");
    onClose();
  };

  return (
    <Modal
      open={!!openTask}
      onCancel={onClose}
      footer={null}
      width={850}
      centered
      bodyStyle={{ padding: 0, borderRadius: 10 }}
    >
      {openTask && (
        <div className="flex">
          {/* LEFT CONTENT */}
          <div className="flex-1 p-6 border-r">
            <div className="flex items-center gap-2 mb-3">
              <CheckCircleFilled className="text-green-500 text-lg" />
              <h3 className="font-semibold text-gray-800 text-lg">
                {openTask.task}
              </h3>
            </div>
            <p className="text-gray-500 mb-5">{openTask.desc}</p>

            {/* Comment Box */}
            <div className="border rounded-md p-3">
              <Input.TextArea
                rows={3}
                placeholder="Comment"
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                className="border-none focus:ring-0 resize-none"
              />
              <div className="flex justify-between items-center mt-2">
                <div className="flex gap-3 text-gray-400 text-lg">
                  <PaperClipOutlined />
                  <AudioOutlined />
                  <SmileOutlined />
                  <EnterOutlined />
                </div>
                <div className="flex gap-2">
                  <Button onClick={onClose}>Cancel</Button>
                  <Button type="primary" danger onClick={handleComment}>
                    Comment
                  </Button>
                </div>
              </div>
            </div>
          </div>

          {/* RIGHT SIDEBAR */}
          <div className="w-72 bg-[#fcfaf8] p-4 space-y-2 border-l">
            <SidebarItem
              label="Project"
              icon={<LockOutlined />}
              value="Inbox"
            />
            <SidebarItem label="Date" value="+" />
            <SidebarItem label="Deadline" icon={<LockOutlined />} />
            <SidebarItem label="Priority" value="P4" />
            <SidebarItem label="Labels" value="+" />
            <SidebarItem label="Reminders" value="+" />
            <SidebarItem label="Location" icon={<LockOutlined />} />
          </div>
        </div>
      )}
    </Modal>
  );
}

function SidebarItem({ label, value, icon }) {
  return (
    <div className="flex items-center justify-between py-1.5 text-sm text-gray-600">
      <div className="flex items-center gap-2">
        {icon || <span className="text-gray-400">📁</span>}
        <span>{label}</span>
      </div>
      <span className="text-gray-500">{value}</span>
    </div>
  );
}
