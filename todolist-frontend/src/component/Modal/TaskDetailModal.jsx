// src/components/TaskDetailModal.jsx
import {
  CheckCircleFilled,
  LockOutlined
} from "@ant-design/icons";
import { Modal } from "antd";
import { useState, useEffect } from "react";
import {https_taskflow} from "../../service/api";
import CommentSection from "../TaskComment/CommentSection";
import dayjs from "dayjs";

export default function TaskDetailModal({
                                          isOpenComment,
                                          openTask,
                                          onClose,
                                          task,
                                          onUpdateStatus
}) {
  const [taskDetail, setTaskDetail] = useState({});

  const handleComment = async (newComment) => {
    console.log("Comment:", newComment);
    try{
      const response = await https_taskflow.post(`/v1/projects/${task.idProject}/tasks/${task.id}/comments`,{
        comment: newComment
      })

      if (response.status === 200) {
        console.log("success:", response.data.data);
        const taskDetailNew = { ...taskDetail, comments: [...taskDetail.comments,response.data.data] };
        setTaskDetail(taskDetailNew);
      }


    }catch(err){
      // Kiểm tra xem server có trả lỗi dạng JSON không
      if (err.response && err.response.data) {
        const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
        alert(msg);
      } else {
        alert("Không thể kết nối đến server. Vui lòng thử lại.");
      }
    }
  };

  const onUpdateComment = async (newComment, idComment) => {
    if (!newComment.trim()) return;
    try{
      const res = await https_taskflow.patch(
          `/v1/projects/${task.idProject}/tasks/comments/${idComment}`,
          { comment: newComment }
      );

      console.log("success:", res.data);

      if (res.status === 200) {
        const commentUpdated = res.data.data;
        const updatedComments = taskDetail.comments.map(comment =>
            comment.id === commentUpdated.id ? commentUpdated : comment
        );
        setTaskDetail(prev => ({ ...prev, comments: updatedComments }));
      }
    }catch(err){
      // Kiểm tra xem server có trả lỗi dạng JSON không
      if (err.response && err.response.data) {
        const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
        alert(msg);
      } else {
        alert("Không thể kết nối đến server. Vui lòng thử lại.");
      }
    }
  };

  const onDeleteComment = async (idComment) => {
    try{
      const res = await https_taskflow.delete(
          `/v1/projects/${task.idProject}/tasks/comments/${idComment}`
      );

      if (res.status === 200) {
        const commentDeleted = res.data.data;
        const updatedComments = (taskDetail.comments || []).filter(
            comment => comment.id !== commentDeleted.id
        );
        setTaskDetail(prev => ({ ...prev, comments: updatedComments }));
      }
    }catch(err){
      // Kiểm tra xem server có trả lỗi dạng JSON không
      if (err.response && err.response.data) {
        const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
        alert(msg);
      } else {
        alert("Không thể kết nối đến server. Vui lòng thử lại.");
      }
    }
  }

  useEffect(() => {
    const getDetails = async () => {
      try{
        const response = await https_taskflow.get(`/v1/projects/${task.idProject}/tasks/${task.id}`)
        setTaskDetail(response.data.data);
      }catch (error) {
        console.log(error);
      }
    }
    getDetails();
  }, [task]);

  return (
    <Modal
      open={!!openTask}
      onCancel={onClose}
      footer={null}
      width={1000}
      centered
      styles={{  body: {padding: 0, borderRadius: 10} }}
    >
      {openTask && (
        <div className="flex">
          {/* LEFT CONTENT */}
          <div className="flex-1 p-6 border-r">
            <div className="flex items-center gap-2 mb-3">
              <input
                  checked={taskDetail.status === "COMPLETED"}
                  type="checkbox"
                  className="cursor-pointer accent-red-500 w-5 h-5 rounded-full"
                  onChange={(e) => {
                    e.stopPropagation();
                    const updatedStatus = taskDetail.status !== "COMPLETED" ? "COMPLETED" : "PENDING"
                    onUpdateStatus(updatedStatus);
                  }}
              />
              <h3 className="font-semibold text-gray-800 text-lg">
                {taskDetail.title}
              </h3>
            </div>
            <p className="text-gray-500 mb-5">{taskDetail.description}</p>

            {/* Comment Box */}
            <CommentSection isOpenComment={isOpenComment} comments={taskDetail.comments} handleComment={handleComment} onUpdateComment={onUpdateComment}  onDeleteComment={onDeleteComment} />
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
