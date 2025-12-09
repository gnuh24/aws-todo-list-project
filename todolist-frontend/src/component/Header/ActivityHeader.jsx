import {
    DownOutlined,
    ProjectOutlined,
    NumberOutlined,
    SmileOutlined,
    UnorderedListOutlined,
    CheckCircleFilled,
    SearchOutlined,
    CheckOutlined, TeamOutlined,
} from "@ant-design/icons";
import { Dropdown, Input, Typography, Divider } from "antd";
import {useEffect, useMemo, useState} from "react";
import {https_taskflow} from "../../service/api";
import {notificationFilterMap} from "../../data/ActivityFilter";

const { Text } = Typography;
export default function ActivityHeader({
                                        selectedCollaboratorID,
                                        selectedTypes,
                                        selectedProjectID,
                                        setSelectedProjectID,
                                        setSelectedCollaboratorID,
                                        setSelectedTypes,
                                        setProjectData
}) {

  const [searchProject, setSearchProject] = useState("");
  const [searchMember, setSearchMember] = useState("");
  const [searchType, setSearchType] = useState("");

  const [selectedProject, setSelectedProject] = useState("All Projects");
  const [selectedFilter, setSelectedFilter] = useState("All Actions");
  const [selectedCollaborator, setSelectedCollaborator] = useState("Everyone");


  const [openProject, setOpenProject] = useState(false);
  const [openCollaborator, setOpenCollaborator] = useState(false);
  const [openFilter, setOpenFilter] = useState(false);

  const [myProjects, setMyProjects] = useState([]);
  const [members, setMembers] = useState([]);

  // hàm để truy vấn
  useEffect(()=>{

  },[selectedCollaboratorID, selectedTypes])

  useEffect(() => {
    const getMyProject = async () => {
      try {
        const response = await https_taskflow.get("/v1/projects");
        if (response.status === 200) {
          const listProject = response.data.data;
          setMyProjects(listProject);
          setSelectedProjectID(listProject.map(p => p.id));
          setSelectedTypes(Object.keys(notificationFilterMap));

            // Chuyển listProject thành object { [id]: name }
            const projectObj = listProject.reduce((acc, project) => {
                acc[project.id] = project.name;
                return acc;
            }, {});

            setProjectData(projectObj);
        }
      } catch (error) {
        console.error(error);
      }
    };

    getMyProject();
  }, []);

  // Filter project theo searchText
  const filteredProjects = useMemo(() => {
    return myProjects.filter((project) =>
        project.name.toLowerCase().includes(searchProject.toLowerCase())
    );
  }, [myProjects, searchProject]);

  const projectMenu = (
      <div className="w-64 p-3 bg-white shadow-lg rounded-md">
        {/* Thanh tìm kiếm */}
        <Input
            size="small"
            placeholder="Search project..."
            className="mb-3 text-sm rounded"
            value={searchProject}
            onChange={(e) => setSearchProject(e.target.value)}
        />

        {/* Mục All Projects */}
        <div
            className="flex items-center gap-2 px-2 py-1 rounded cursor-pointer hover:bg-gray-100"
            onClick={() => {
              setSelectedProject("All Projects")
              setSelectedProjectID(myProjects.map(p => p.id))
              setOpenProject(false)
              setSearchProject("")
            }}
        >
          <NumberOutlined /> All Projects
        </div>

        <Divider className="my-2" />

        {/* Tiêu đề My Projects */}
        <Text type="secondary" className="pl-2 text-xs uppercase">
          My Projects
        </Text>

        {/* Danh sách project filter */}
        <div className="mt-2 max-h-64 overflow-y-auto space-y-1">
          {filteredProjects.length > 0 ? (
              filteredProjects.map((project) => (
                  <div
                      key={project.id}
                      className="flex items-center gap-2 px-2 py-1 rounded cursor-pointer hover:bg-gray-100"
                      onClick={() => {
                        setSelectedProject(project.name)
                        setSelectedProjectID([project.id])
                        setOpenProject(false)
                        setSearchProject("")
                      }}
                  >
                    <ProjectOutlined /> {project.name}
                  </div>
              ))
          ) : (
              <Text type="secondary" className="px-2 text-xs">
                No projects found
              </Text>
          )}
        </div>
      </div>
  );

  // --- Collaborator Dropdown ---

    useEffect(() => {
        const getMembers = async () => {
            try {
                let allMembers = [];

                for (const projectId of selectedProjectID) {
                    const res = await https_taskflow.get(`/v1/projects/${projectId}/members`);
                    if (res.status === 200) {
                        allMembers = [...allMembers, ...res.data.data];
                    }
                }

                // Lọc trùng theo accountId
                const map = new Map();
                allMembers.forEach(m => map.set(m.accountId, m));
                const uniqueMembers = Array.from(map.values());

                // Set state 1 lần duy nhất
                setMembers(uniqueMembers);

                // Lấy tất cả accountId
                const allIds = uniqueMembers.map(m => m.accountId);

                if (
                    selectedCollaboratorID.length === 0 || // nếu chưa chọn gì
                    !selectedCollaboratorID.every(id => allIds.includes(id)) // nếu có ID không còn trong uniqueMembers
                ) {
                    // Set lại Everyone
                    setSelectedCollaborator("Everyone");
                    setSelectedCollaboratorID(allIds);
                }

            } catch (e) {
                console.error(e);
            }
        };

        getMembers();

    }, [selectedProjectID]);

    const filteredMembers = members.filter(member =>
        member.displayName.toLowerCase().includes(searchMember.toLowerCase())
    );

    const collaboratorMenu = (
        <div className="bg-white border rounded-md shadow-lg w-56 p-2">
            <Input
                size="small"
                prefix={<SearchOutlined />}
                placeholder="Filter by collaborator"
                className="mb-2"
                value={searchMember}
                onChange={e => setSearchMember(e.target.value)}
            />
            <ul>
                {/* Lựa chọn Everyone */}
                <li
                    key="everyone"
                    onClick={() => {
                        setSelectedCollaborator("Everyone")
                        setSelectedCollaboratorID(members.map(m => m.accountId))
                        setSearchMember("")
                        setOpenCollaborator(false)
                    }}
                    className={`flex items-center justify-between px-3 py-1.5 text-[13px] rounded cursor-pointer hover:bg-gray-100 ${
                        selectedCollaborator === "Everyone" ? "bg-gray-50" : ""
                    }`}
                >
                    <div className="flex items-center gap-2">
                        <span>Everyone</span>
                    </div>
                    {selectedCollaborator === "Everyone" && (
                        <CheckOutlined className="text-red-500 text-xs" />
                    )}
                </li>

                {/* Danh sách members */}
                {filteredMembers.map((member) => (
                    <li
                        key={member.id}
                        onClick={() => {
                            setSelectedCollaborator(member.displayName)
                            setSelectedCollaboratorID([member.accountId])
                            setSearchMember("")
                            setOpenCollaborator(false)
                        }}
                        className={`flex items-center justify-between px-3 py-1.5 text-[13px] rounded cursor-pointer hover:bg-gray-100 ${
                            selectedCollaborator === member.displayName ? "bg-gray-50" : ""
                        }`}
                    >
                        <div className="flex items-center gap-2">
                            {member.avatar ? (
                                <img
                                    src={member.avatar}
                                    alt={member.displayName}
                                    className="w-6 h-6 rounded-full object-cover"
                                />
                            ) : (
                                <div className="w-6 h-6 rounded-full bg-green-500 flex items-center justify-center text-xs font-semibold">
                                    {member.displayName?.charAt(0).toUpperCase()}
                                </div>
                            )}
                            <span>{member.displayName}</span>
                        </div>
                        {selectedCollaborator === member.displayName && (
                            <CheckOutlined className="text-red-500 text-xs" />
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );


    // --- Activity Filter Dropdown ---

    // Chuyển object thành mảng để filter
    const filterItems = Object.entries(notificationFilterMap)
        .map(([key, value]) => ({
            id: key,
            label: value.label,
            icon: value.icon
        }))
        .filter(item =>
            item.label.toLowerCase().includes(searchType.toLowerCase())
        );

// JSX menu
    const filterMenu = (
        <div className="bg-white border rounded-md shadow-lg w-56 p-2">
            <Input
                size="small"
                prefix={<SearchOutlined />}
                placeholder="Filter by type"
                className="mb-2"
                value={searchType}
                onChange={e => setSearchType(e.target.value)}
            />

            <ul className="max-h-64 overflow-y-auto">
                {/* --- All actions --- */}
                <li
                    key="ALL"
                    onClick={() => {
                        setSelectedFilter("All Actions");
                        setSelectedTypes(Object.keys(notificationFilterMap));
                        setOpenFilter(false);
                        setSearchType("");
                    }}
                    className={`flex items-center gap-2 px-3 py-1.5 rounded cursor-pointer text-[13px] hover:bg-gray-100 ${
                        selectedFilter === "All Actions" ? "bg-gray-50 font-medium" : ""
                    }`}
                >
                    <UnorderedListOutlined className="text-gray-500" />
                    <span>All Actions</span>
                    {selectedFilter === "All Actions" && (
                        <CheckCircleFilled className="ml-auto text-green-500 text-xs" />
                    )}
                </li>

                {/* --- Các notification khác --- */}
                {filterItems.map(item => (
                    <li
                        key={item.id}
                        onClick={() => {
                            setSelectedFilter(item.label);
                            setSelectedTypes([item.id]);
                            setOpenFilter(false);
                            setSearchType("");
                        }}
                        className={`flex items-center gap-2 px-3 py-1.5 rounded cursor-pointer text-[13px] hover:bg-gray-100 ${
                            selectedFilter === item.label ? "bg-gray-50 font-medium" : ""
                        }`}
                    >
                        {item.icon}
                        <span>{item.label}</span>
                        {selectedFilter === item.label && (
                            <CheckCircleFilled className="ml-auto text-green-500 text-xs" />
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );


    return (
      <div className="flex items-center justify-between mb-8">
        {/* Left: Title */}
        <div className="flex items-center gap-2">
          <h3 className="text-xl font-semibold flex items-center gap-2">
            Activity:{" "}
            <Dropdown
                overlay={projectMenu}
                trigger={["click"]}
                open={openProject}
                onOpenChange={setOpenProject}
            >
            <span
                className="font-normal cursor-pointer hover:underline select-none flex items-center gap-1"
                onClick={(e) => e.stopPropagation()} // ngăn event bubble nếu cần
            >
              {selectedProject} <DownOutlined className="text-xs" />
            </span>
            </Dropdown>
          </h3>
        </div>

        {/* Right: Filters */}
        <div className="flex items-center gap-5 text-gray-600">
          <Dropdown
              overlay={collaboratorMenu}
              trigger={["click"]}
              open={openCollaborator}
              onOpenChange={setOpenCollaborator}
          >
          <span className="flex items-center gap-1 cursor-pointer
                 hover:bg-gray-200  hover:text-black
                 transition-colors duration-200 px-1 py-1 rounded">
              <TeamOutlined /> {selectedCollaborator}
          </span>
          </Dropdown>

          <Dropdown
              overlay={filterMenu}
              trigger={["click"]}
              open={openFilter}
              onOpenChange={setOpenFilter}
          >
          <span className="flex items-center gap-1 cursor-pointer
                 hover:bg-gray-200  hover:text-black
                 transition-colors duration-200 px-1 py-1 rounded">
            <SmileOutlined /> {selectedFilter}
          </span>
          </Dropdown>
        </div>
      </div>
  );
}
