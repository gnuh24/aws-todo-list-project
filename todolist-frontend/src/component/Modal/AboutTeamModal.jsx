import { Modal, Select, Button } from "antd";
import { useState } from "react";

const industries = [
  { label: "Technology", value: "technology" },
  { label: "Education", value: "education" },
  { label: "Healthcare", value: "healthcare" },
  { label: "Other", value: "other" },
];

const works = [
  { label: "Software Development", value: "software" },
  { label: "Design", value: "design" },
  { label: "Marketing", value: "marketing" },
  { label: "Other", value: "other" },
];

const roles = [
  { label: "Manager", value: "manager" },
  { label: "Developer", value: "developer" },
  { label: "Designer", value: "designer" },
  { label: "Other", value: "other" },
];

export default function AboutTeamModal({ open, onCancel, teamName, onCreate }) {
  const [industry, setIndustry] = useState("");
  const [work, setWork] = useState("");
  const [role, setRole] = useState("");

  const handleCreateTeam = () => {
    onCreate({ teamName, industry, work, role });
  };

  return (
    <Modal
      open={open}
      onCancel={onCancel}
      footer={null}
      width={700}
      centered
      className="custom-modal"
    >
      <div className="flex">
        {/* Left */}
        <div className="flex-1 p-6">
          <h2 className="text-[18px] font-semibold mb-1">About {teamName}</h2>
          <p className="text-[13px] text-gray-600 mb-5">
            Your answers will tailor your experience.
          </p>

          {/* Industry */}
          <label className="text-[13px] font-medium mb-1 block">
            What industry do you work in?
          </label>
          <Select
            value={industry}
            onChange={setIndustry}
            placeholder="Select your answer"
            options={industries}
            className="w-full mb-4 text-[13px]"
          />

          {/* Work */}
          <label className="text-[13px] font-medium mb-1 block">
            What work do you do?
          </label>
          <Select
            value={work}
            onChange={setWork}
            placeholder="Select your answer"
            options={works}
            className="w-full mb-4 text-[13px]"
          />

          {/* Role */}
          <label className="text-[13px] font-medium mb-1 block">
            What’s your role?
          </label>
          <Select
            value={role}
            onChange={setRole}
            placeholder="Select your answer"
            options={roles}
            className="w-full mb-5 text-[13px]"
          />

          <Button
            type="primary"
            onClick={handleCreateTeam}
            disabled={!industry || !work || !role}
            className="w-full h-9 bg-[#f87070] border-none hover:bg-[#ff6e6e]"
          >
            Create your team
          </Button>

          <p className="text-[12px] text-gray-500 mt-3 text-center">
            By creating a team, you agree to our{" "}
            <a href="#" className="text-blue-500 underline">
              Terms of Service
            </a>
            . Each team workspace in Todoist is billed separately.
          </p>
        </div>

        {/* Right */}
        <div className="w-[45%] bg-[#fffaf6] p-6 rounded-r-lg border-l">
          <h3 className="font-medium mb-4 text-[14px]">
            <img
              src="https://d3ptyyxy2at9ui.cloudfront.net/assets/images/51cd449521ea45e9.png"
              alt=""
            />
            Team productivity, made simple
          </h3>
          <ul className="text-[13px] space-y-2 text-gray-700">
            <li>✔ A shared team workspace</li>
            <li>✔ Task and project collaboration</li>
            <li>✔ Centralized access and billing</li>
            <li>✔ Team and project activity trends</li>
            <li>✔ Shared project templates</li>
            <li>✔ All personal productivity tools</li>
          </ul>
        </div>
      </div>
    </Modal>
  );
}
