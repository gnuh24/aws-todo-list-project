import React, { useState } from "react";
import { Input, Button, Dropdown } from "antd";
import { CalendarOutlined, BellOutlined } from "@ant-design/icons";
import DatePickerDropdown from "../Dropdown/DatePickerDropdown";
import PriorityDropdown from "../Dropdown/PriorityDropdown";
import MoreOptionsDropdown from "../Dropdown/MoreOptionsDropdown";
import ProjectSelectDropdown from "../Dropdown/ProjectSelectDropdown";

export default function InlineAddTaskFormUpComing({ onCancel, onAdd }) {
  const [taskName, setTaskName] = useState("");
  const [description, setDescription] = useState("");
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedProject, setSelectedProject] = useState("Inbox");

  return (
    <div className="border rounded-lg p-4 mt-2 w-full max-w-xl bg-white shadow-sm">
      {/* Task input */}
      <Input
        placeholder="Meet with tutor Friday at 3pm"
        value={taskName}
        onChange={(e) => setTaskName(e.target.value)}
        className="border-none text-[14px] font-medium focus:shadow-none"
      />

      {/* Description */}
      <Input.TextArea
        placeholder="Description"
        autoSize={{ minRows: 1, maxRows: 3 }}
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        className="border-none text-[13px] text-gray-500 mt-1 mb-3 focus:shadow-none"
      />

      {/* Action buttons row */}
      <div className="flex flex-wrap gap-2 mb-3">
        <Dropdown
          trigger={["click"]}
          dropdownRender={() => (
            <DatePickerDropdown onSelect={(val) => setSelectedDate(val)} />
          )}
        >
          <Button icon={<CalendarOutlined />} size="small">
            {selectedDate ? selectedDate.toString() : "Date"}
          </Button>
        </Dropdown>

        <PriorityDropdown onSelect={(p) => console.log("Priority:", p)} />
        <Button icon={<BellOutlined />} size="small">
          Reminders
        </Button>
        <MoreOptionsDropdown
          onSelect={(a) => console.log("Option selected:", a)}
        />
      </div>

      <hr />

      {/* Footer actions */}
      <div className="flex justify-between items-center mt-3">
        <ProjectSelectDropdown
          selected={selectedProject}
          onSelect={(proj) => setSelectedProject(proj)}
        />

        <div className="flex gap-2">
          <Button size="small" onClick={onCancel}>
            Cancel
          </Button>
          <Button
            type="primary"
            danger
            size="small"
            disabled={!taskName.trim()}
            onClick={() => {
              onAdd({
                title: taskName,
                description,
                date: selectedDate,
                project: selectedProject,
              });
              setTaskName("");
              setDescription("");
            }}
          >
            Add task
          </Button>
        </div>
      </div>
    </div>
  );
}
