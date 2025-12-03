import { Modal, Input, Select, Switch, Button } from "antd";
import { useState } from "react";
import { https_taskflow } from "../../service/api";
import AboutTeamModal from "./AboutTeamModal";
import CreateTeamModal from "./CreateTeamModal";

const colors = [
  { label: "Charcoal", value: "charcoal" },
  { label: "Red", value: "red" },
  { label: "Blue", value: "blue" },
  { label: "Green", value: "green" },
];

const parentProjects = [
  { label: "No Parent", value: "No Parent" },
  { label: "My Projects", value: "My Projects" },
  { label: "# Fullstack", value: "Fullstack" },
];

export default function AddProjectModal({ open, onCancel, onAdd }) {
  const [name, setName] = useState("");
  const [color, setColor] = useState("charcoal");
  const [workspace, setWorkspace] = useState("My Projects");
  const [parent, setParent] = useState("No Parent");
  const [favorite, setFavorite] = useState(false);
  const [openCreateTeam, setOpenCreateTeam] = useState(false);
  const [openAboutTeam, setOpenAboutTeam] = useState(false);
  const [teamName, setTeamName] = useState("");

  const handleAdd = async () => {
    if (!name.trim()) return alert("Please enter a project name");

    try {
      const payload = {
        name,
        isArchived: true,
      };

      const res = await https_taskflow.post("/v1/projects", payload);

      if (
        res.data?.status === 200 ||
        res.status === 200 ||
        res.status === 201
      ) {
        alert("Project added successfully!");
        onAdd?.(res.data.data || payload);
        setName("");
        onCancel();
      } else {
      }
    } catch (err) {
      console.error(err);
    } finally {
    }
  };

  return (
    <Modal
      title={<span className="font-medium text-[15px]">Add project</span>}
      open={open}
      onCancel={onCancel}
      footer={null}
      width={420}
      className="custom-modal"
    >
      <div className="space-y-5">
        {/* Name */}
        <div>
          <label className="text-[13px] text-gray-700 font-medium mb-1 block">
            Name
          </label>
          <Input
            placeholder="Enter project name"
            value={name}
            maxLength={120}
            onChange={(e) => setName(e.target.value)}
            className="rounded-md text-[13px] h-8"
          />
          <div className="text-right text-xs text-gray-400 mt-1">
            {name.length}/120
          </div>
        </div>

        {/* Color */}
        <div>
          <label className="text-[13px] text-gray-700 font-medium mb-1 block">
            Color
          </label>
          <Select
            value={color}
            onChange={setColor}
            options={colors}
            className="w-full text-[13px]"
            dropdownStyle={{ fontSize: 13 }}
          />
        </div>

        {/* Workspace */}
        <div>
          <label className="text-[13px] text-gray-700 font-medium mb-1 block">
            Workspace
          </label>
          <Select
            value={workspace}
            onChange={setWorkspace}
            className="w-full text-[13px]"
            dropdownRender={(menu) => (
              <div>
                {menu}
                <div
                  onClick={() => setOpenCreateTeam(true)}
                  className="px-3 py-2 text-[13px] text-red-500 hover:bg-gray-50 cursor-pointer border-t"
                >
                  Add team workspace
                </div>
              </div>
            )}
            options={[
              {
                label: (
                  <div className="flex items-center gap-2">
                    <img
                      src="https://i.pravatar.cc/24"
                      alt="workspace"
                      className="rounded-full w-5 h-5"
                    />
                    <span>My Projects</span>
                  </div>
                ),
                value: "My Projects",
              },
            ]}
          />
        </div>

        {/* Parent project */}
        <div>
          <label className="text-[13px] text-gray-700 font-medium mb-1 block">
            Parent project
          </label>
          <Select
            showSearch
            value={parent}
            onChange={setParent}
            placeholder="Type a project name"
            className="w-full text-[13px]"
            dropdownStyle={{ fontSize: 13 }}
            options={parentProjects.map((item) => ({
              label: (
                <span
                  className={`${
                    item.label === "# Fullstack" ? "text-[#4f46e5]" : ""
                  }`}
                >
                  {item.label}
                </span>
              ),
              value: item.value,
            }))}
          />
        </div>

        {/* Add to favorites */}
        <div className="flex items-center justify-between">
          <label className="text-[13px] text-gray-700 font-medium">
            Add to favorites
          </label>
          <Switch
            checked={favorite}
            onChange={setFavorite}
            size="small"
            className="custom-switch"
          />
        </div>

        {/* Buttons */}
        <div className="flex justify-end gap-2 pt-4">
          <Button
            onClick={onCancel}
            className="rounded-md text-[13px] px-4 h-8"
          >
            Cancel
          </Button>
          <Button
            type="primary"
            onClick={handleAdd}
            className="rounded-md text-[13px] px-5 h-8 bg-[#f87070] border-none hover:bg-[#ff6e6e]"
          >
            Add
          </Button>
        </div>
      </div>
      {/* Modal tạo team */}
      <CreateTeamModal
        open={openCreateTeam}
        onCancel={() => setOpenCreateTeam(false)}
        onContinue={(teamName) => {
          setOpenCreateTeam(false);
          setTimeout(() => {
            setTeamName(teamName);
            setOpenAboutTeam(true);
          }, 200); // delay nhẹ cho smooth
        }}
      />
      <AboutTeamModal
        open={openAboutTeam}
        onCancel={() => setOpenAboutTeam(false)}
        teamName={teamName}
        onCreate={(data) => {
          console.log("Team created:", data);
          setWorkspace(data.teamName);
          setOpenAboutTeam(false);
        }}
      />
    </Modal>
  );
}
