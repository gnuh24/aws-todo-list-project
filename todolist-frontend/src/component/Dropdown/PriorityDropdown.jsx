import React, { useState } from "react";
import { Dropdown, Menu, Button } from "antd";
import { FlagOutlined, CheckOutlined } from "@ant-design/icons";

export default function PriorityDropdown({ onSelect }) {
  const [selected, setSelected] = useState(4); // mặc định là Priority 4 (thấp nhất)

  const priorities = [
    { id: 1, color: "text-red-500", label: "Priority 1" },
    { id: 2, color: "text-orange-400", label: "Priority 2" },
    { id: 3, color: "text-blue-500", label: "Priority 3" },
    { id: 4, color: "text-gray-400", label: "Priority 4" },
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
              setSelected(p.id);
              onSelect?.(p.id);
            }}
          >
            <span className="flex items-center gap-2">
              <FlagOutlined className={p.color} />
              {p.label}
            </span>
            {selected === p.id && <CheckOutlined className="text-red-500" />}
          </div>
        ),
      }))}
    />
  );

  const current = priorities.find((p) => p.id === selected);

  return (
    <Dropdown overlay={menu} trigger={["click"]} placement="bottomLeft">
      <Button icon={<FlagOutlined className={current.color} />} size="small">
        Priority
      </Button>
    </Dropdown>
  );
}
