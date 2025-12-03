import React, { useState } from "react";
import { Dropdown, Menu, Button } from "antd";
import { FlagOutlined, CheckOutlined } from "@ant-design/icons";

export default function PriorityDropdown({ priority, onSelect }) {

    const priorities = [
        { id: "CRITICAL", color: "text-red-700", label: "Critical" },   // Cực kỳ khẩn cấp
        { id: "HIGH",     color: "text-red-500", label: "High" },       // Quan trọng
        { id: "MEDIUM",   color: "text-orange-400", label: "Medium" },  // Bình thường
        { id: "LOW",      color: "text-blue-500", label: "Low" }        // Thấp
    ];



    const menu = (
    <Menu
      className="rounded-lg shadow-lg p-1"
      items={priorities.map((p) => ({
        key: p.id,
        label: (
          <div
            className="flex justify-between items-center w-36 px-2 py-1 rounded-md hover:bg-gray-100 cursor-pointer"
            onClick={() => {
                onSelect?.(p.id);
            }}
          >
            <span className="flex items-center gap-2">
              <FlagOutlined className={p.color} />
              {p.label}
            </span>
            { priority === p.id && <CheckOutlined className="text-red-500" />}
          </div>
        ),
      }))}
    />
  );

  const current = priorities.find((p) => p.id === priority);

  return (
    <Dropdown overlay={menu} trigger={["click"]} placement="bottomLeft">
      <Button icon={<FlagOutlined className={current.color} />} size="small">
          {current.label}
      </Button>
    </Dropdown>
  );
}
