import { useRef, useState, useEffect } from "react";
import MainLayout from "../../layout/MainLayout";
import { LeftOutlined, RightOutlined } from "@ant-design/icons";
import { ChevronDown } from "lucide-react";
import { DayPicker, getDefaultClassNames } from "react-day-picker";
import "react-day-picker/dist/style.css";
import InlineAddTaskFormUpComing from "../../component/Modal/InlineAddTaskFormUpComing";
import TaskItem from "../../component/Task/TaskItem";
import { https_taskflow } from "../../service/api";

export default function UpcomingPage() {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [showAddTaskIndex, setShowAddTaskIndex] = useState(null);
  const [showCalendar, setShowCalendar] = useState(false);
  const [tasksByDate, setTasksByDate] = useState({});

  const [selected, setSelected] = useState();

  const defaultClassNames = getDefaultClassNames();

  // Lấy ra ngày chủ nhật của tuần hiện tại
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const startOfWeekCurrent = new Date(today);
  startOfWeekCurrent.setDate(today.getDate() - today.getDay()); // Chủ Nhật đầu tuần của hiện tại

  // Lấy ra ngày chủ nhật của tuần đang được chọn
  const startOfSelectedWeek = new Date(selectedDate);
  startOfSelectedWeek.setDate(selectedDate.getDate() - selectedDate.getDay());
  startOfSelectedWeek.setHours(0, 0, 0, 0);

  // Tạo danh sách 7 ngày cho tuần tiếp theo tính từ tuần hiện tại
  const days = Array.from({ length: 7 }, (_, i) => {
    const date = new Date(startOfSelectedWeek);
    date.setDate(startOfSelectedWeek.getDate() + i); // +0 → CN, +1 → T2, ...
    const weekday = date.toLocaleDateString("en-US", { weekday: "short" });
    const day = date.getDate();
    return { day, weekday, date };
  });

  // Tuần sẽ lùi tới nếu click prev
  const newStartWeek = new Date(startOfSelectedWeek);
  newStartWeek.setDate(startOfSelectedWeek.getDate() - 7);

  // Disable nếu tuần sẽ lùi qua tuần hiện tại
  const isPrevDisabled = newStartWeek < startOfWeekCurrent;

  // Chuyển tuần trước
  const prevWeek = () => {
    const newDate = new Date(selectedDate);
    newDate.setDate(selectedDate.getDate() - 7);
    setSelectedDate(newDate);
  };

  // Chuyển tuần sau
  const nextWeek = () => {
    const newDate = new Date(selectedDate);
    newDate.setDate(selectedDate.getDate() + 7);
    setSelectedDate(newDate);
  };

  // Lưu ref cho từng ngày
  const dayRefs = useRef({});

  // Khi chọn ngày, cuộn đến đúng ref
  useEffect(() => {
    if (selectedDate) {
      const dateKey = selectedDate.toDateString();
      const targetElement = dayRefs.current[dateKey];

      if (targetElement) {
        // Lấy chiều cao của phần header ghim
        const headerOffset = 400; // ←←← CẦN CHỈNH CHO PHÙ HỢP

        const elementPosition = targetElement.getBoundingClientRect().top;
        const offsetPosition = window.scrollY + elementPosition - headerOffset;

        window.scrollTo({
          top: offsetPosition,
          behavior: "smooth",
        });
      }
    }
  }, [selectedDate]);

  // Fetch api để lấy danh sách các task sắp đến của user
  useEffect(() => {
    const groupTasksByDate = (tasks) => {
      return tasks.reduce((acc, task) => {
        const dateKey = new Date(task.startTime).toDateString(); // "Tue Nov 05 2025"
        if (!acc[dateKey]) {
          acc[dateKey] = [];
        }
        acc[dateKey].push(task);
        return acc;
      }, {});
    };

    const fetchTasks = async () => {
      try {
        const response = await https_taskflow.get("/v1/projects/taskUpComing");
        // kiểm tra status
        if (response.status !== 200) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        // response.data là danh sách task
        setTasksByDate(groupTasksByDate(response.data.data));
      } catch (err) {
        console.error("Error fetching tasks:", err);
      }
    };

    fetchTasks();
  }, []);

  return (
    <MainLayout>

 
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

        {/* Phần nội dung cuộn được */}
        <div className="flex-1 overflow-y-auto">
          <div className="space-y-8">
            {days
              .filter((d) => d.date >= today)
              .map((d, i) => (
                <div
                  key={i}
                  ref={(el) => (dayRefs.current[d.date.toDateString()] = el)}
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

                    <div className="ml-4 flex flex-col gap-2">
                      {tasksByDate[d.date.toDateString()]?.map((task, idx) => (
                        <div
                          key={idx}
                          onClick={() => setShowAddTaskIndex(null)}
                        >
                          <TaskItem
                            task={task}
                            projectId={task.idProject}
                            sectionId={task.idSection}
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
                            isOpenFormAddTaskUpComing={showAddTaskIndex}
                          />
                        </div>
                      ))}

                      {showAddTaskIndex !== i && (
                        <button
                          onClick={() => setShowAddTaskIndex(i)}
                          className="flex items-center gap-2 text-sm font-medium text-gray-500 hover:text-red-500 transition duration-200 ease-in-out group focus:outline-none"
                        >
                          <span className="flex items-center justify-center text-red-500 group-hover:bg-red-500 group-hover:text-white w-6 h-6 rounded-full transition duration-200 ease-in-out">
                            ＋
                          </span>
                          <span>Add task</span>
                        </button>
                      )}
                    </div>

                    {showAddTaskIndex === i && (
                      <div className="ml-4 mt-2">
                        <InlineAddTaskFormUpComing
                          initialDate={d.date}
                          onCancel={() => {
                            setShowAddTaskIndex(null);
                          }}
                          onAdd={(content) => {
                            const dateKey = new Date(
                              content.startTime
                            ).toDateString();
                            setTasksByDate((prev) => ({
                              ...prev,
                              [dateKey]: [...(prev[dateKey] || []), content],
                            }));
                            setShowAddTaskIndex(null);
                          }}
                        />
                      </div>
                    )}
                  </div>
                </div>
              ))}
          </div>
        </div>
      </div>
         </MainLayout>
  );
}
