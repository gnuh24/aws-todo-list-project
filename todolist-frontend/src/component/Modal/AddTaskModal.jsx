import React, { useState } from "react";
import { Modal, Input, Button, Dropdown } from "antd";
import { CalendarOutlined, BellOutlined } from "@ant-design/icons";

import DatePickerDropdown from "../Dropdown/DatePickerDropdown";
import PriorityDropdown from "../Dropdown/PriorityDropdown";
import MoreOptionsDropdown from "../Dropdown/MoreOptionsDropdown";
import ProjectSelectDropdown from "../Dropdown/ProjectSelectDropdown";

export default function AddTaskModal({
  onSelectProjectSection,
  open,
  onCancel,
  onAdd,
}) {
  const [taskName, setTaskName] = useState("");
  const [description, setDescription] = useState("");
  const [selectedDate, setSelectedDate] = useState(null);

  // -------------------------
  // NEW: selected lưu cả object
  // -------------------------
  const [selectedProject, setSelectedProject] = useState({
    projectId: null,
    projectName: "Inbox",
    sectionId: null,
    sectionName: null,
  });

  return (
    <Modal
      open={open}
      onCancel={onCancel}
      footer={null}
      centered
      width={550}
      className="rounded-xl"
    >
      <div className="flex flex-col gap-3">
        {/* Task title */}
        <Input
          placeholder="Practice math problems daily at 4pm"
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
          className="border-none text-[13px] text-gray-500 focus:shadow-none"
        />

        {/* Buttons row */}
        <div className="flex items-center gap-2">
          <Dropdown
            trigger={["click"]}
            dropdownRender={() => (
              <DatePickerDropdown
                onSelect={(value) => setSelectedDate(value)}
              />
            )}
          >
            <Button icon={<CalendarOutlined />} size="small">
              {selectedDate ? selectedDate.format("DD/MM/YYYY") : "Date"}
            </Button>
          </Dropdown>

          <PriorityDropdown
            onSelect={(p) => console.log("Priority selected:", p)}
          />

          <Button icon={<BellOutlined />} size="small">
            Reminders
          </Button>

          <MoreOptionsDropdown
            onSelect={(action) => console.log("Chọn:", action)}
          />
        </div>

        <hr />

        {/* Bottom section */}
        <div className="flex justify-between items-center">
          <ProjectSelectDropdown
            selected={selectedProject}
            onSelect={(data) => {
              // data = { projectId, projectName, sectionId, sectionName }
              setSelectedProject(data);
              onSelectProjectSection?.(data);
            }}
          />

          <div className="flex gap-2">
            <Button onClick={onCancel}>Cancel</Button>
            <Button
              type="primary"
              danger
              disabled={!taskName.trim()}
              onClick={() => {
                onAdd({
                  title: taskName,
                  description,
                  deadline: selectedDate,
                  project: selectedProject,
                });
console.log("Selected:", selectedDate);

                setTaskName("");
                setDescription("");
                setSelectedDate(null);
              }}
            >
              Add task11111111111111
            </Button>
          </div>
        </div>
      </div>
    </Modal>
  );
}
