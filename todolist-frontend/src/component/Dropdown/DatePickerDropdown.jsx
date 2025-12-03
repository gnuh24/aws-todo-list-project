// DatePickerDropdown.jsx
import { useState } from "react";
import { Calendar, Divider, Button } from "antd";
import dayjs from "dayjs";
import {
  SunOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  ReloadOutlined,
  CloseOutlined
} from "@ant-design/icons";

export default function DatePickerDropdown({ isStartTime, onSelect, showForm}) {

    const currentTime = dayjs();
    let today = null;

    // Các mốc ngày
    if (isStartTime) {
        today = currentTime.hour(0).minute(0).second(0);
    }else{
        today = currentTime.hour(23).minute(59).second(59);
    }

    const tomorrow = currentTime.add(1, "day");

    // Tính thứ của cuối tuần (Saturday)
    const thisWeekend = currentTime.day() <= 6
        ? currentTime.day(6) // set về thứ 6 (0=CN, 6=Sat)
        : currentTime.add(1, "week").day(6); // nếu đã qua thứ 6 tuần này thì sang tuần sau

    const nextWeek = currentTime.add(1, "week").day(1); // Monday tuần sau


  return (showForm &&
        <div className="bg-white rounded-md shadow-lg w-72 p-2">
            {/* Quick options */}
            <div className="space-y-1 text-[13px]">
                <div
                    className="flex justify-between items-center hover:bg-gray-100 rounded px-2 py-1 cursor-pointer"
                    onClick={() =>
                        {onSelect(today)}
                    }
                >
              <span>
                <SunOutlined className="text-orange-400 mr-2" /> Today
              </span>
                    <span className="text-gray-400">{today.format("ddd, DD/MM/YYYY")}</span>
                </div>
                <div
                    className="flex justify-between items-center hover:bg-gray-100 rounded px-2 py-1 cursor-pointer"
                    onClick={() =>
                    {onSelect(tomorrow)}
                    }
                >
              <span>
                <CalendarOutlined className="text-green-500 mr-2" /> Tomorrow
              </span>
                    <span className="text-gray-400">{tomorrow.format("ddd, DD/MM/YYYY")}</span>
                </div>
                <div
                    className="flex justify-between items-center hover:bg-gray-100 rounded px-2 py-1 cursor-pointer"
                    onClick={() => {
                        onSelect(thisWeekend)
                    }
                    }
                >
              <span>
                <CalendarOutlined className="text-purple-500 mr-2" /> This weekend
              </span>
                    <span className="text-gray-400">{thisWeekend.format("ddd, DD/MM")}</span>
                </div>
                <div
                    className="flex justify-between items-center hover:bg-gray-100 rounded px-2 py-1 cursor-pointer"
                    onClick={() =>
                    {onSelect(nextWeek)}
                    }
                >
              <span>
                <CalendarOutlined className="text-blue-500 mr-2" /> Next week
              </span>
                    <span className="text-gray-400">{nextWeek.format("ddd, DD/MM")}</span>
                </div>
            </div>

          <Divider className="my-2" />

          {/* Calendar */}
          <Calendar
              fullscreen={false}
              onSelect={(date) => {
                if (isStartTime) {
                    onSelect(dayjs(date).hour(0).minute(0).second(0));
                }else {
                    onSelect(dayjs(date).hour(23).minute(59).second(59));
                }
              }}
              disabledDate={(currentDate) => {
                  // currentDate là dayjs object
                  return currentDate.isBefore(dayjs(), "day"); // chặn ngày trước hôm nay
              }}
          />

          <Divider className="my-2" />

          {/* Time & Repeat */}
          <div className="flex justify-between items-center">
            <Button size="small" icon={<ClockCircleOutlined />}>
              Time
            </Button>
              <Button
                  className="hover:bg-red-100 rounded px-2 py-1 cursor-pointer"
                  size="small"
                  icon={<CloseOutlined />} // đổi icon thành dấu X hoặc icon hủy
                  onClick={() => {
                      onSelect(null);
                  }}
              >
                  Cancel
              </Button>
          </div>
        </div>
  );
}
