import React, { useState } from "react";
import MainLayout from "../../layout/MainLayout";
import { LeftOutlined, RightOutlined } from "@ant-design/icons";
import { ChevronDown } from "lucide-react";
import { DayPicker, getDefaultClassNames } from "react-day-picker";
import "react-day-picker/dist/style.css";
import InlineAddTaskFormUpComing from "../../component/Modal/InlineAddTaskFormUpComing";

export default function UpcomingPage() {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [showAddTaskIndex, setShowAddTaskIndex] = useState(null);
  const [taskContent, setTaskContent] = useState("");
  const [showCalendar, setShowCalendar] = useState(false);
  const [selected, setSelected] = useState();

  const defaultClassNames = getDefaultClassNames();
  // ✅ Tạo danh sách 7 ngày kế tiếp dựa trên selectedDate
  const days = Array.from({ length: 7 }, (_, i) => {
    const date = new Date(selectedDate);
    date.setDate(selectedDate.getDate() + i);
    const weekday = date.toLocaleDateString("en-US", { weekday: "short" });
    const day = date.getDate();
    return { day, weekday, date };
  });

  return (
    <MainLayout>
      <div className="min-h-screen bg-white px-8 py-6">
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-semibold text-gray-800">Upcoming</h1>
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
            <button className="border border-gray-300 rounded px-3 py-1 text-sm hover:bg-gray-100">
              <LeftOutlined />
            </button>
            <button
              onClick={() => setSelectedDate(new Date())}
              className="border border-gray-300 rounded px-3 py-1 text-sm hover:bg-gray-100"
            >
              Today
            </button>
            <button className="border border-gray-300 rounded px-3 py-1 text-sm hover:bg-gray-100">
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
              classNames={{
                today: "border-red-500  rounded-full",
                selected: "bg-red-500 text-white  rounded-full",
                chevron: "fill-gray-300",
              }}
            />
          </div>
        )}

        {/* Thanh tuần */}
        <div className="flex items-center gap-6 border-b pb-2 mb-6 overflow-x-auto">
          {days.map((d, index) => (
            <div
              key={index}
              className="flex flex-col items-center min-w-[60px]"
            >
              <span
                className={`text-sm ${
                  index === 0 ? "text-red-600 font-medium" : "text-gray-500"
                }`}
              >
                {d.weekday}
              </span>
              <div
                className={`mt-1 px-2 py-[2px] rounded-md text-sm ${
                  index === 0
                    ? "bg-red-100 text-red-600 font-semibold"
                    : "text-gray-700"
                }`}
              >
                {d.day}
              </div>
            </div>
          ))}
        </div>

        {/* Danh sách ngày + Add task */}
        <div className="space-y-8">
          {days.map((d, i) => (
            <div key={i} className="border-b pb-6">
              <div className="flex flex-col gap-2">
                <div className="flex items-center justify-between">
                  <p className="text-[13px] text-gray-700 font-medium">
                    {d.day} {d.date.toLocaleString("en-US", { month: "short" })}{" "}
                    · {d.weekday}
                  </p>

                  {showAddTaskIndex !== i && (
                    <button
                      onClick={() => setShowAddTaskIndex(i)}
                      className="flex items-center gap-2 text-[13px] text-red-500 hover:text-red-600"
                    >
                      <span className="text-lg leading-none">＋</span>
                      <span>Add task</span>
                    </button>
                  )}
                </div>

                {showAddTaskIndex === i && (
                  <InlineAddTaskFormUpComing
                    initialValue={taskContent}
                    onCancel={() => {
                      setShowAddTaskIndex(null);
                      setTaskContent("");
                    }}
                    onAdd={(content) => {
                      alert(`Added: ${content}`);
                      setShowAddTaskIndex(null);
                      setTaskContent("");
                    }}
                  />
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </MainLayout>
  );
}
