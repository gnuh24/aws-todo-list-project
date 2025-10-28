import React from "react";
import { Button, Space } from "antd";
import { CalendarOutlined, CloseOutlined } from "@ant-design/icons";

export default function TodayHeader() {
  return (
    <div className="flex justify-between items-center px-6 py-4  bg-white ">
      {/* Bên trái */}
      <h1 className="text-xl font-semibold text-gray-800">Today</h1>

      {/* Bên phải */}
      <Space size="middle">
        <Button
          type="default"
          icon={<CalendarOutlined />}
          className="flex items-center"
        >
          Connect calendar
        </Button>
        <Button type="text" icon={<CloseOutlined />}>
          Display
        </Button>
      </Space>
    </div>
  );
}
