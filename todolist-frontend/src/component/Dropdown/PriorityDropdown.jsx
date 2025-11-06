import React, { useState } from "react";
import { Dropdown, Menu, Button } from "antd";
import { FlagOutlined, CheckOutlined } from "@ant-design/icons";

export default function PriorityDropdown({ priority, setPriority }) {

  const priorities = [
    { id: "HIGH", color: "text-red-500", label: "Priority 1" },
    { id: "MEDIUM", color: "text-orange-400", label: "Priority 2" },
    { id: "LOW", color: "text-blue-500", label: "Priority 3" }
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
                setPriority?.(p.id);
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
