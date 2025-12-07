
import {useEffect, useState} from "react";
import { LockOutlined } from "@ant-design/icons";
import ActivityHeader from "../../component/Header/ActivityHeader";
import TaskDetailModal from "../../component/Modal/TaskDetailModal";
import SpinnerForSettings from "../../component/Spinner/SpinnerForSettings";
import {https_notification, https_taskflow} from "../../service/api";
import {useNavigate} from "react-router-dom";
import {notificationFilterMap} from "../../data/ActivityFilter";
import { toast } from "sonner";
import MainLayout from "../../layout/MainLayout";

function formatDateHeader(dateStr) {
  const date = new Date(dateStr);

  const day = date.getDate();

  // Tháng viết tắt tiếng Anh
  const monthShort = date.toLocaleString("en-US", { month: "short" });

  // Lấy thứ tiếng Anh (Monday, Tuesday, ...)
  const weekday = date.toLocaleString("en-US", { weekday: "long" });

  return `${day} ${monthShort} · ${weekday}`;
}


export default function CompletedPage() {
  const navigate = useNavigate();
  const [openTask, setOpenTask] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [currentTask, setCurrentTask] = useState({});

  const [selectedProjectID, setSelectedProjectID] = useState([]);
  const [selectedCollaboratorID, setSelectedCollaboratorID] = useState([]);
  const [selectedTypes, setSelectedTypes] = useState([]);

  const [activityData, setActivityData] = useState([]);
  const [projectData, setProjectData] = useState([]);

  const handleOpenTask = (task) => {
    if (task.id !== null) {
      setCurrentTask(task)
      setOpenTask(true)
    }
  };
  const handleClose = () => {
    setOpenTask(false)
  };

  const handleUpdateStatus = async (updatedStatus) => {
    try {
      const res = await https_taskflow.patch(
          `/v1/projects/${currentTask.idProject}/tasks/${currentTask.id}/update-status`,
          {
            status: updatedStatus,
          }
      );

    } catch (err) {
      toast.error(err?.response?.data?.message || "Cập nhật thất bại, vui lòng thử lại!");
    }
  }

  useEffect(() => {
    const getActivityData = async () => {
      setIsLoading(true);
      try{
        const res = await https_notification.post("/v1/activity",{
          projectIds: selectedProjectID,
          accountIds: selectedCollaboratorID,
          types: selectedTypes,
        })

        if (res.status === 200) {
          setActivityData(res.data.data)
          setIsLoading(false);
        }

      }catch (error) {
        console.log(error);
      }
    }
    getActivityData();

  },[selectedCollaboratorID, selectedTypes, selectedProjectID]);


  const handleClickProject = (projectName, projectId) => {
    // Ví dụ navigate tới trang project với id
    navigate(`/app/projects/${encodeURIComponent(projectName)}/${projectId}`);
  };

  return (
      <>
      <MainLayout>

     
        <div className="px-10 py-6">
          <div className="sticky top-0 z-30 bg-white py-1">
            <ActivityHeader
                selectedTypes={selectedTypes}
                selectedCollaboratorID={selectedCollaboratorID}
                selectedProjectID={selectedProjectID}
                setSelectedProjectID={setSelectedProjectID}
                setSelectedTypes={setSelectedTypes}
                setSelectedCollaboratorID={setSelectedCollaboratorID}
                setProjectData={setProjectData}
            />
          </div>



          <div className="space-y-5">
            {isLoading ? (
                <SpinnerForSettings />
            ) : (
                <>
                  {/*Chuyển về dạng danh sách mảng 2 chiều rồi mới chạy vòng lặp/*/}
                  {Object.entries(activityData).map(([date, items]) => (
                      <div key={date} className="mb-8">

                        {/* --- Header Ngày --- */}
                        <div className="sticky top-16 z-20 bg-white mb-2 font-semibold text-gray-800 text-sm border-b border-gray-200 py-1">
                          {formatDateHeader(date)}
                        </div>

                        {/* --- Danh sách activity của ngày đó --- */}
                        {items.map((item) => (
                            <div
                                key={item.id}
                                className="flex items-start justify-between border-b pb-4 py-2 px-2 rounded-2xl mb-2"
                            >
                              <div className="flex items-start gap-3">

                                {/* Avatar */}
                                <div className="relative">
                                  {item.avatar ? (
                                      <img
                                          src={item.avatar}
                                          alt={item.displayName}
                                          className="w-14 h-14 rounded-full object-cover"
                                      />
                                  ) : (
                                      <div className="w-14 h-14 rounded-full bg-green-500 flex items-center justify-center text-xs font-semibold">
                                        {item.displayName?.charAt(0).toUpperCase()}
                                      </div>
                                  )}
                                  <div className="absolute -bottom-1 -right-1 w-6 h-6 flex items-center justify-center text-green-500 bg-white rounded-full shadow-md">
                                    {notificationFilterMap[item.type]?.icon}
                                  </div>
                                </div>

                                {/* Nội dung */}
                                <div>
                                  <p className="text-sm" onClick={() =>  handleOpenTask({id: item.taskId, idProject: item.projectId})}>
                                    <span className="font-semibold">{item.displayName}</span>
                                  </p>


                                  <p
                                      className={`text-gray-400 ${item.taskId ? 'cursor-pointer hover:text-[#E52424]' : ''}`}
                                      onClick={() =>  handleOpenTask({ id: item.taskId, idProject: item.projectId })}
                                  >
                                    <span className="text-xs mt-1 font-semibold">
                                      {item.content}
                                    </span>
                                  </p>


                                  <p className="text-xs text-gray-400 mt-1">
                                    {new Date(item.createdAt).toLocaleTimeString("vi-VN", {
                                      hour: "2-digit",
                                      minute: "2-digit"
                                    })}
                                  </p>
                                </div>
                              </div>

                              {/* Project lock icon */}
                              <div
                                  className="text-xs text-gray-500 flex items-center gap-1 cursor-pointer hover:text-[#E52424] transition-colors"
                                  onClick={(e)=> {
                                    e.stopPropagation();
                                    handleClickProject(projectData[item.projectId], item.projectId)
                                  }}
                              >
                                {projectData[item.projectId]}
                                <LockOutlined className="text-[10px]" />
                              </div>
                            </div>
                        ))}
                      </div>
                  ))}

                  {Object.keys(activityData).length === 0 && (
                      <div className="flex flex-col items-center justify-center h-full text-center px-4">

                        {/* Hình minh họa */}
                        <img
                            src="/NoActivity.png" // thay bằng ảnh bạn muốn
                            alt="No activity"
                            className="w-80 h-80 mb-6"
                        />

                        {/* 3 dòng text */}
                        <p className="text-lg font-semibold mb-2">
                          No activity in the past week.
                        </p>
                        <p className="text-gray-500 mb-2">
                          See all changes that have been made in your account, by you or your collaborators.
                        </p>
                        <p className="text-gray-500">
                          Free users can still view their completed tasks inside any project.
                        </p>
                      </div>
                  )}


                  <div className="text-center text-gray-500 text-sm py-5 border-t border-gray-200">
                    That's it. No more history to load.
                  </div>


                  {openTask && (
                      <TaskDetailModal
                          openTask={openTask}
                          task={currentTask}
                          onClose={handleClose}
                          onUpdateStatus={handleUpdateStatus}
                      />
                  )}

                </>
            )}
          </div>
        </div>
         </MainLayout>
      </>
  );

}
