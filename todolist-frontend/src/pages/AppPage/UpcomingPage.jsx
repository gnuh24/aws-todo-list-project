import { useRef, useState, useEffect } from "react";
import { LeftOutlined, RightOutlined } from "@ant-design/icons";
import { ChevronDown } from "lucide-react";
import { DayPicker } from "react-day-picker";
import "react-day-picker/dist/style.css";
import { https_taskflow } from "../../service/api";
import TaskItemForUpComing from "../../component/Task/TaskItemForUpComing";
import { useNavigate, useSearchParams } from "react-router-dom";
import { toast } from "sonner";
import TaskDetailModal from "../../component/Modal/TaskDetailModal";
import AddTaskModal from "../../component/Modal/AddTaskModal";

export default function UpcomingPage() {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [openAddTaskDate, setOpenAddTaskDate] = useState(null);
  const [showCalendar, setShowCalendar] = useState(false);
  const [tasksByDate, setTasksByDate] = useState({});
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const [selectedProject, setSelectedProject] = useState(null);
  const [selectedSection, setSelectedSection] = useState(null);




  // ============================
  // GOOGLE LOGIN CALLBACK
  // ============================
  useEffect(() => {
    const token = params.get("token");
    if (!token) return;

    localStorage.setItem("accessToken", token);
    localStorage.setItem("refreshToken", params.get("refreshToken"));

    toast.success("Đăng nhập thành công!");
    setTimeout(() => navigate("/app/upcoming"), 800);
  }, [params, navigate]);

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const startOfWeekCurrent = new Date(today);
  startOfWeekCurrent.setDate(today.getDate() - today.getDay());

  const startOfSelectedWeek = new Date(selectedDate);
  startOfSelectedWeek.setDate(selectedDate.getDate() - selectedDate.getDay());
  startOfSelectedWeek.setHours(0, 0, 0, 0);

  const days = Array.from({ length: 7 }, (_, i) => {
    const date = new Date(startOfSelectedWeek);
    date.setDate(startOfSelectedWeek.getDate() + i);
    return {
      day: date.getDate(),
      weekday: date.toLocaleDateString("en-US", { weekday: "short" }),
      date,
    };
  });

  // Tuần sẽ lùi tới nếu click prev
  const newStartWeek = new Date(startOfSelectedWeek);
  newStartWeek.setDate(startOfSelectedWeek.getDate() - 7);

  // Disable nếu tuần sẽ lùi qua tuần hiện tại
  const isPrevDisabled = newStartWeek < startOfWeekCurrent;

  const prevWeek = () => {
    const d = new Date(selectedDate);
    d.setDate(d.getDate() - 7);
    setSelectedDate(d);
    setOpenAddTaskDate(null);
  };

  const nextWeek = () => {
    const d = new Date(selectedDate);
    d.setDate(d.getDate() + 7);
    setSelectedDate(d);
    setOpenAddTaskDate(null);
  };

  const dayRefs = useRef({});

  useEffect(() => {
    const key = selectedDate.toDateString();
    const el = dayRefs.current[key];
    if (!el) return;

    const offset = el.getBoundingClientRect().top + window.scrollY - 400;
    window.scrollTo({ top: offset, behavior: "smooth" });
  }, [selectedDate]);

  useEffect(() => {
    const groupByDate = (tasks) =>
        tasks.reduce((acc, t) => {
          const key = new Date(t.startTime).toDateString();
          acc[key] = [...(acc[key] || []), t];
          return acc;
        }, {});

    https_taskflow.get("/v1/projects/taskUpComing").then((res) => {
      setTasksByDate(groupByDate(res.data.data));
      console.log(res.data);
    });
  }, []);

  const handleAddTask = async (newTask) => {
    try {
      if (!selectedProject || !selectedSection) {
        toast.warning("Vui lòng chọn Project & Section!");
        return;
      }

      const res = await https_taskflow.post(
          `/v1/projects/${selectedProject}/tasks`,
          {
            title: newTask.title,
            description: newTask.description || "",
            sectionId: selectedSection,
            startTime: newTask.startTime,
            deadline: newTask.deadline || null,
            priority: newTask.priority || "MEDIUM",
            idAccountAssign: newTask.idAccountAssign,
          }
      );

      const task = res.data.data;

      const key = new Date(task.startTime).toDateString();

      setTasksByDate((prev) => ({
        ...prev,
        [key]: [...(prev[key] || []), task],
      }));

      toast.success("Thêm task thành công!");
    } finally {
      setSelectedProject(null);
      setSelectedSection(null);
      setOpenAddTaskDate(null);
    }
  };

  return (
    <>
      <div className="min-h-screen bg-white px-8 pb-6 flex flex-col">
        {/* Phần header và điều khiển (sẽ được ghim) */}
        <div className="sticky top-0 z-10 bg-white pb-4">
          {/* Header */}
          <div className="flex items-center justify-between mb-4">
            <div className="flex pt-4 items-center gap-2">
              <h1 className="text-2xl  font-semibold text-gray-800">
                Upcoming
              </h1>
              <button
                onClick={() => setShowCalendar(!showCalendar)}
                className="p-1 hover:bg-gray-100 rounded-full transition"
              >
                <ChevronDown
                  className={`w-5 h-5 transition-transform duration-200 ${
                    showCalendar ? "rotate-180" : ""
                  }`}
                />
              </button>
            </div>

            <div className="flex items-center gap-2">
              <button
                className={`border border-gray-300 rounded px-3 py-1 text-sm transition${
                  isPrevDisabled
                    ? "opacity-50 cursor-not-allowed bg-gray-100"
                    : "hover:bg-gray-100 active:bg-gray-200"
                }`}
                onClick={prevWeek}
                disabled={isPrevDisabled}
              >
                <LeftOutlined />
              </button>
              <button
                onClick={() => setSelectedDate(new Date())}
                className="border border-gray-300 rounded px-3 py-1 text-sm hover:bg-gray-100"
              >
                Today
              </button>
              <button
                className="border border-gray-300 rounded px-3 py-1 text-sm hover:bg-gray-100"
                onClick={nextWeek}
              >
                <RightOutlined />
              </button>
            </div>
          </div>

          {/* Calendar toggle */}
          {showCalendar && (
            <div className="mb-6 border rounded-md shadow-sm p-2">
              <DayPicker
                animate
                mode="single"
                selected={selectedDate}
                onSelect={(date) => date && setSelectedDate(date)}
                disabled={{ before: new Date() }}
                classNames={{
                  today: "border-red-500 rounded-full",
                  selected: "bg-red-500 text-white rounded-full",
                  chevron: "fill-gray-300",
                }}
              />
            </div>
          )}

          {/* Thanh tuần */}
          <div className="flex items-center gap-6 border-b pb-2 mb-6">
            {days.map((d, index) => {
              const isPast = d.date < today;
              const isSelected =
                d.date.toDateString() === selectedDate.toDateString();
              return (
                <div
                  key={index}
                  className={`flex flex-col items-center min-w-[60px] cursor-pointer ${
                    isPast ? "opacity-50 cursor-not-allowed" : ""
                  }`}
                  onClick={() => {
                    if (!isPast) setSelectedDate(d.date);
                  }}
                >
                  <span
                    className={`text-sm ${
                      isSelected
                        ? "text-red-600 font-medium"
                        : isPast
                        ? "text-gray-400"
                        : "text-gray-700"
                    }`}
                  >
                    {d.weekday}
                  </span>
                  <div
                    className={`mt-1 px-2 py-[2px] rounded-md text-sm ${
                      isSelected
                        ? "bg-red-100 text-red-600 font-semibold"
                        : isPast
                        ? "text-gray-400"
                        : "text-gray-700"
                    }`}
                  >
                    {d.day}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* ================= CONTENT ================= */}
        <div className="flex-1 overflow-y-auto">
          <div className="space-y-8">
            {days
                .filter((d) => d.date >= today)
                .map((d) => {
                  const dateKey = d.date.toDateString();

                  return (
                      <div
                          key={dateKey}
                          ref={(el) => (dayRefs.current[dateKey] = el)}
                          className="pb-6"
                      >

                        <div className="flex flex-col gap-2 pb-6">
                          <div className="flex items-center justify-between border-b pb-3">
                            <div className="flex items-center gap-2">
                              <div className="font-bold px-1 py-1 rounded">
                                {d.day}
                              </div>
                              <div className="text-gray-700 font-medium">
                                {d.date.toLocaleString("en-US", { month: "short" })} ·{" "}
                                {d.weekday}
                              </div>
                            </div>
                          </div>
                        </div>

                        <div className="ml-4 flex flex-col gap-2">
                          {tasksByDate[dateKey]?.map((task) => (
                              <div
                                  key={task.id}
                                  onClick={() => setOpenAddTaskDate(null)}
                              >
                                <TaskItemForUpComing
                                    task={task}
                                    projectId={task.idProject}
                                    onDeleteTaskUpComing={(taskDelete) => {
                                      const dateKey = new Date(
                                          taskDelete.startTime
                                      ).toDateString();

                                      setTasksByDate((prev) => ({
                                        ...prev,
                                        [dateKey]: (prev[dateKey] || []).filter(
                                            (task) => task.id !== taskDelete.id
                                        ),
                                      }));
                                    }}
                                    onUpdateTaskUpComing={(taskUpdate) => {
                                      const dateKey = new Date(
                                          task.startTime
                                      ).toDateString();
                                      const newDateKey = new Date(
                                          taskUpdate.startTime
                                      ).toDateString();

                                      // Nếu vị trí cũ và mới trùng nhau thì cũng sẽ xóa vị trí cũ và thêm phần tử mới vào đúng chỗ đó

                                      setTasksByDate((prev) => {
                                        // Xóa task cũ ở dateKey
                                        const oldTasks = (prev[dateKey] || []).filter(
                                            (task) => task.id !== taskUpdate.id
                                        );

                                        // Thêm taskUpdate vào newDateKey
                                        const newTasks =
                                            newDateKey === dateKey
                                                ? [...oldTasks, taskUpdate] // nếu date không đổi, thêm vào mảng đã filter
                                                : [...(prev[newDateKey] || []), taskUpdate]; // nếu date thay đổi, thêm vào mảng mới

                                        return {
                                          ...prev,
                                          [dateKey]: oldTasks,
                                          [newDateKey]: newTasks,
                                        };
                                      });
                                    }}
                                />
                              </div>
                          ))}

                          {openAddTaskDate !== dateKey && (
                              <button
                                  onClick={() => setOpenAddTaskDate(dateKey)}
                                  className="flex items-center gap-2 text-sm font-medium text-gray-500 hover:text-red-500 group"
                              >
                          <span className="flex items-center justify-center text-red-500 group-hover:bg-red-500 group-hover:text-white w-6 h-6 rounded-full">
                            ＋
                          </span>
                                <span>Add task</span>
                              </button>
                          )}
                        </div>

                        {openAddTaskDate === dateKey && (
                            <div className="ml-4 mt-2">
                              <AddTaskModal
                                  open = {openAddTaskDate}
                                  initialDate={d.date}
                                  onCancel={() => setOpenAddTaskDate(null)}
                                  onAdd={handleAddTask}
                                  onSelectProjectSection={(data) => {
                                    setSelectedProject(data.projectId);
                                    setSelectedSection(data.sectionId);
                                  }}
                              />
                            </div>
                        )}
                      </div>
                  );
                })}
          </div>
        </div>
      </div>



      {/* render 1 lần duy nhất */}
      <TaskDetailModal />

    </>
  );
}
