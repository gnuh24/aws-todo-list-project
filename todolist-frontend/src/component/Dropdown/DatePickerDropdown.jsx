// DatePickerDropdown.jsx
import { Calendar, Divider, Button } from "antd";
import {
  SunOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  ReloadOutlined,
} from "@ant-design/icons";

export default function DatePickerDropdown({ onSelect }) {
  return (
    <div className="bg-white rounded-md shadow-lg w-72 p-2">
      {/* Quick options */}
      <div className="space-y-1 text-[13px]">
        <div
          className="flex justify-between items-center hover:bg-gray-100 rounded px-2 py-1 cursor-pointer"
          onClick={() => onSelect("today")}
        >
          <span>
            <SunOutlined className="text-orange-400 mr-2" /> Today
          </span>
          <span className="text-gray-400">Tue</span>
        </div>
        <div
          className="flex justify-between items-center hover:bg-gray-100 rounded px-2 py-1 cursor-pointer"
          onClick={() => onSelect("tomorrow")}
        >
          <span>
            <CalendarOutlined className="text-green-500 mr-2" /> Tomorrow
          </span>
          <span className="text-gray-400">Wed</span>
        </div>
        <div
          className="flex justify-between items-center hover:bg-gray-100 rounded px-2 py-1 cursor-pointer"
          onClick={() => onSelect("weekend")}
        >
          <span>
            <CalendarOutlined className="text-purple-500 mr-2" /> This weekend
          </span>
          <span className="text-gray-400">Sat</span>
        </div>
        <div
          className="flex justify-between items-center hover:bg-gray-100 rounded px-2 py-1 cursor-pointer"
          onClick={() => onSelect("nextweek")}
        >
          <span>
            <CalendarOutlined className="text-blue-500 mr-2" /> Next week
          </span>
          <span className="text-gray-400">Mon 27 Oct</span>
        </div>
      </div>

      <Divider className="my-2" />

      {/* Calendar */}
      <Calendar fullscreen={false} onSelect={(date) => onSelect(date)} />

      <Divider className="my-2" />

      {/* Time & Repeat */}
      <div className="flex justify-between items-center">
        <Button size="small" icon={<ClockCircleOutlined />}>
          Time
        </Button>
        <Button size="small" icon={<ReloadOutlined />}>
          Repeat
        </Button>
      </div>
    </div>
  );
}
