import React, { useState } from "react";
import { Input, Button, Dropdown } from "antd";
import {
  CalendarOutlined,
  FlagOutlined,
  BellOutlined,
  MoreOutlined,
  InboxOutlined,
} from "@ant-design/icons";
import DatePickerDropdown from "../Dropdown/DatePickerDropdown";
import PriorityDropdown from "../Dropdown/PriorityDropdown";
import MoreOptionsDropdown from "../Dropdown/MoreOptionsDropdown";
import ProjectSelectDropdown from "../Dropdown/ProjectSelectDropdown";

export default function TaskEditForm({ task, onSave, onCancel }) {
  const [taskName, setTaskName] = useState(task?.title || "");
  const [description, setDescription] = useState(task?.description || "");
  const [selectedDate, setSelectedDate] = useState(task?.date || null);
  const [selectedProject, setSelectedProject] = useState(
    task?.project || "Inbox"
  );
  const [priority, setPriority] = useState(task?.priority || null);

  const handleSave = () => {
    if (!taskName.trim()) return;
    onSave?.({
      ...task,
      title: taskName,
      description,
      date: selectedDate,

      project: selectedProject,
      priority,
    });
  };

  return (
    <div className="border rounded-lg p-3 bg-white shadow-sm w-full">
      {/* Title input */}
      <Input
        placeholder="Edit task name..."
        value={taskName}
        onChange={(e) => setTaskName(e.target.value)}
        className="border-none text-[15px] font-medium focus:shadow-none"
      />

      {/* Description */}
      <Input.TextArea
        placeholder="Description"
        autoSize={{ minRows: 1, maxRows: 3 }}
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        className="border-none text-[13px] text-gray-500 focus:shadow-none mt-2"
      />

      {/* Buttons row */}
      <div className="flex items-center gap-2 mt-3 flex-wrap">
        {/* Date picker */}
        <Dropdown
          trigger={["click"]}
          dropdownRender={() => (
            <DatePickerDropdown onSelect={(value) => setSelectedDate(value)} />
          )}
        >
          <Button icon={<CalendarOutlined />} size="small">
            {selectedDate ? selectedDate.toString() : "Date"}
          </Button>
        </Dropdown>

        {/* Priority */}
        <PriorityDropdown onSelect={(p) => setPriority(p)} />

        {/* Reminder */}
        <Button icon={<BellOutlined />} size="small">
          Reminder
        </Button>

        {/* More options */}
        <MoreOptionsDropdown
          onSelect={(action) => console.log("Selected:", action)}
        />
      </div>

      <hr className="my-3" />

      {/* Bottom section */}
      <div className="flex justify-between items-center">
        {/* Project select */}
        <ProjectSelectDropdown
          selected={selectedProject}
          onSelect={(project) => setSelectedProject(project)}
        />

        {/* Action buttons */}
        <div className="flex gap-2">
          <Button onClick={onCancel}>Cancel</Button>
          <Button
            type="primary"
            danger
            disabled={!taskName.trim()}
            onClick={handleSave}
          >
            Save
          </Button>
        </div>
      </div>
    </div>
  );
}
