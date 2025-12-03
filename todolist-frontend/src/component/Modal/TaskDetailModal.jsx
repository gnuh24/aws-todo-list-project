import {
  LockOutlined
} from "@ant-design/icons";
import { Modal, Tooltip} from "antd";
import { useState, useEffect } from "react";
import {https_taskflow} from "../../service/api";
import CommentSection from "../TaskComment/CommentSection";
import {LabelsSection} from "../Section/LabelsSection";
import PriorityDropdown from "../Dropdown/PriorityDropdown";
import {toast} from "sonner";

export default function TaskDetailModal({
                                          isOpenComment,
                                          openTask,
                                          onClose,
                                          task,
                                          onUpdateStatus,
}) {
  const [taskDetail, setTaskDetail] = useState({});

  const handleComment = async (newComment, attachments) => {
    try{
      const response = await https_taskflow.post(`/v1/projects/${task.idProject}/tasks/${task.id}/comments`,{
        comment: newComment,
        urls: attachments
      })

      if (response.status === 200) {
        const taskDetailNew = { ...taskDetail, comments: [...taskDetail.comments,response.data.data] };
        setTaskDetail(taskDetailNew);
      }


    }catch(err){
      // Kiểm tra xem server có trả lỗi dạng JSON không
      if (err.response && err.response.data) {
        const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
        toast.error(msg);
      } else {
        toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
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
        toast.error(msg);
      } else {
        toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
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
        toast.error(msg);
      } else {
        toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
      }
    }
  }

  const onDeleteCommentAttach = async (url) => {
    try{
      const res = await https_taskflow.delete(
          `/v1/projects/${task.idProject}/deleteCommentAttach`,{
            params: {
              fileUrl: url
            }
          }
      );

      if (res.status === 200) {
        const commentAttachDeleted = res.data.data;
        const updatedComments = (taskDetail.comments || []).map(comment => {
          // nếu đây là comment chứa attachment vừa xóa
          if (comment.id === commentAttachDeleted.taskCommentId) {
            return {
              ...comment,
              commentAttach: (comment.commentAttach || []).filter(
                  att => att.id !== commentAttachDeleted.id
              )
            };
          }
          return comment;
        });
        setTaskDetail(prev => ({ ...prev, comments: updatedComments }));
      }
    }catch(err){
      // Kiểm tra xem server có trả lỗi dạng JSON không
      if (err.response && err.response.data) {
        const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
        toast.error(msg);
      } else {
        toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
      }
    }
  }

  const onUpdatePriority = async (newPriority) => {
    try{
      const res = await https_taskflow.patch(
          `/v1/projects/${task.idProject}/tasks/${task.id}/update-priority`,{
            priority: newPriority
          }
      );

      if (res.status === 200) {
        setTaskDetail(prev => ({ ...prev, priority: newPriority }));
      }
    }catch(err){
      // Kiểm tra xem server có trả lỗi dạng JSON không
      if (err.response && err.response.data) {
        const msg = err.response.data.message || err.response.data.detailMessage || "Đã xảy ra lỗi không xác định";
        toast.error(msg);
      } else {
        toast.info("Không thể kết nối đến server. Vui lòng thử lại.");
      }
    }
  }


  useEffect(() => {
    const getDetails = async () => {
      try {
        const response = await https_taskflow.get(
            `/v1/projects/${task.idProject}/tasks/${task.id}`
        );
        setTaskDetail(prev => ({ ...response.data.data }));
      } catch (error) {
        console.log(error);
      }
    };

    getDetails();
  }, [task]); // chạy lại khi openTask hoặc task thay đổi


  return (
    <Modal
      open={!!openTask}
      onCancel={onClose}
      footer={null}
      width={1000}
      high={800}
      centered
      styles={{  body: {padding: 15, borderRadius: 10} }}
    >
      {Object.keys(taskDetail).length > 0 && (
        <div className="flex">
          {/* LEFT CONTENT */}
          <div className="flex-1 p-6 border-r">
            <div className="flex items-center gap-2 mb-3">
              <input
                  checked={taskDetail.status === "COMPLETED"}
                  type="checkbox"
                  className="cursor-pointer accent-red-500 w-5 h-5 rounded-full"
                  onChange={async (e) => {
                    e.stopPropagation();
                    const updatedStatus = taskDetail.status !== "COMPLETED" ? "COMPLETED" : "PENDING"
                    if (await onUpdateStatus(updatedStatus)) {
                      setTaskDetail(prev => ({...prev, status: updatedStatus}));
                    }
                  }}
              />
              <h3 className="font-semibold text-gray-800 text-lg">
                {taskDetail.title}
              </h3>
            </div>
            <p className="text-gray-500 mb-5">{taskDetail.description}</p>

            {/* Comment Box */}
            <CommentSection isOpenComment={isOpenComment} comments={taskDetail.comments} handleComment={handleComment} onUpdateComment={onUpdateComment}  onDeleteComment={onDeleteComment} onDeleteCommentAttach={onDeleteCommentAttach}/>
          </div>

          {/* RIGHT SIDEBAR */}
          <div className="w-72 bg-[#fcfaf8] p-4 space-y-2 border-l">
            <SidebarItem
              label="Project"
              icon={<LockOutlined />}
              value="Inbox"
            />
            {/* Date */}
            <SidebarItem label="Date" value="+"
                         onClick={() => console.log("Open date picker")}
            />

            {/* Deadline */}
            <SidebarItem
                label="Deadline"
                icon={<LockOutlined />}
                onClick={() => console.log("Deadline locked")}
            />

            {/* Priority */}
            <SidebarItem
                label="Priority"
            >
              <PriorityDropdown priority={taskDetail.priority} onSelect={onUpdatePriority} />
            </SidebarItem>


            {/* Labels with dropdown */}
            <LabelsSection taskDetail={taskDetail}></LabelsSection>

            {/* Reminders */}
            <SidebarItem
                label="Reminders"
                value="+"
                onClick={() => console.log("Reminders")}
            />

            {/* Location */}
            <SidebarItem
                label="Location"
                icon={<LockOutlined />}
                onClick={() => console.log("Location locked")}
            />
          </div>
        </div>
      )}
    </Modal>
  );
}


export function SidebarItem({ label, value, icon, onClick, children }) {
  return (
      <Tooltip title={label}>
        {/* Đường kẻ phân cách */}
        <div className="border-t border-gray-200 my-1" />
        <div
            className="flex justify-between items-center cursor-pointer px-3 py-2 hover:bg-gray-100 rounded-lg"
            onClick={onClick}
        >
          <div className="flex items-center gap-2">
            {icon}
            <span>{label}</span>
          </div>

          <div className="text-gray-600">
            {value ? <span>{value}</span> : children}
          </div>
        </div>

      </Tooltip>


  );
}
