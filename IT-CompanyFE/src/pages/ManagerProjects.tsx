import { ChangeEvent, SyntheticEvent, useEffect, useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import Table from 'react-bootstrap/Table';
import { useNavigate } from 'react-router-dom';


interface Project {
  id: number;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  employeeProjects: EmployeeProject[];
}

interface EmployeeDetail {
  user: User;
  jobDescription: string;
  startDate: string;
  endDate: string;
}

interface EmployeeProject {
  id: number;
  jobDescription: string;
  startDate: string;
  endDate: string;
  user: {
    id: number;
    firstname: string;
    lastname: string;
  };
}

interface User {
  userId: number;
  firstname: string;
  lastname: string;
  email: string;
  phoneNumber: string;
  title: boolean;
  role: Role;
  address: Address;
}

interface Address {
  street: string;
  number: string;
  city: string;
  country: string;
}

interface Role {
  id: number;
  name: string;
}

function ManagerProjects() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedEmployees, setSelectedEmployees] = useState<User[]>([]);
  const [employees, setEmployees] = useState<User[]>([]);
  const [employeeDetails, setEmployeeDetails] = useState<EmployeeDetail[]>([]);

  //const [startDate, setStartDate] = useState('');
  //const [endDate, setEndDate] = useState('');
 // const [description, setDescription] = useState('');

  const navigate = useNavigate();

  const [edit, setEdit] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [projectName, setProjectName] = useState("");
  const [id, setProjectId] = useState(-1);
  const [jobDescription, setJobDescription] = useState("");
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  useEffect(() => {
    fetch('https://localhost:8081/api/v1/user/getManagerProjects', {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
      },
      credentials: 'include',
    })
      .then((response) => response.json())
      .then((data) => {
        setProjects(data);
      })
      .catch((error) => console.log(error));
  }, []);

  const handleEdit = async (event: SyntheticEvent) => {
    event.preventDefault();

    await fetch('https://localhost:8081/api/v1/user/editEmployessOnProject', {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        id: id,
        userId: selectedEmployees[0].userId,
        jobDescription: jobDescription,
        startDate: startDate,
        endDate:endDate,
        // users: selectedEmployees
      }),
    })
      .then((response) => {
        if (response.ok) {
          setEdit(false); // Isključite način izmene nakon uspešnog čuvanja
        }
      })
      .catch((error) => console.log(error));

    navigate("/managerProjects");
    // window.location.reload();
    console.log(isEditMode);
  };

  const handleRemoveEmployee = (employeeId: number) => {
    setEmployeeDetails((prevEmployeeDetails) => prevEmployeeDetails.filter((employee) => employee.user.userId !== employeeId));
  };

  const handleEmployeeChange = (event: ChangeEvent<HTMLSelectElement>) => {
    const { options } = event.target;
    const selectedEmployeeIds = Array.from(options)
      .filter((option) => option.selected)
      .map((option) => {
        const userId = parseInt(option.value);
        return employees.find((employee) => employee.userId === userId) || null;
      });
    setSelectedEmployees(selectedEmployeeIds.filter((employee): employee is User => employee !== null));
  };

  

  const handleAddEmployee = () => {
    const selectedEmployee = employees.find((employee) => selectedEmployees.includes(employee));
    if (selectedEmployee) {
      const employeeAlreadyAdded = employeeDetails.some((employee) => employee.user.userId === selectedEmployee.userId);
      if (!employeeAlreadyAdded) {
        const newEmployeeDetails: EmployeeDetail = {
          user: selectedEmployee,
          jobDescription: '',
          startDate: '',
          endDate: '',
        };
        setEmployeeDetails([...employeeDetails, newEmployeeDetails]);
      } else {
        console.log('Employee already added to the project.');
      }
    }
  };


  

  // const handleJobDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>, employeeId: number) => {
  //   const { value } = event.target;
  //   setEmployeeDetails((prevEmployeeDetails) =>
  //     prevEmployeeDetails.map((employee) =>
  //       employee.user.userId === employeeId ? { ...employee, jobDescription: value } : employee
  //     )
  //   );
  // };

  const editChange = (project: Project): void => {
    
    if (!validateDates(project)) {
      return;
    }
    setEdit(true);
    setIsEditMode(true);
    setProjectName(project.name);
    setProjectId(project.id);
    fetch('https://localhost:8081/api/v1/user/getAll', {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
      },
      credentials: 'include',
    })
      .then((response) => response.json())
      .then((data) => {
        setEmployees(data);
      })
      .catch((error) => console.log(error));
  };

  // const handleStartDateChange = (event: React.ChangeEvent<HTMLInputElement>, employeeId: number) => {
  //   const { value } = event.target;
  //   setEmployeeDetails((prevEmployeeDetails) =>
  //     prevEmployeeDetails.map((employee) =>
  //       employee.user.userId === employeeId ? { ...employee, startDate: value } : employee
  //     )
  //   );
  // };

  // const handleEndDateChange = (event: React.ChangeEvent<HTMLInputElement>, employeeId: number) => {
  //   const { value } = event.target;
  //   setEmployeeDetails((prevEmployeeDetails) =>
  //     prevEmployeeDetails.map((employee) =>
  //       employee.user.userId === employeeId ? { ...employee, endDate: value } : employee
  //     )
  //   );
  // };

  const validateDates = (project: Project): boolean => {
    /* if (startDate && endDate && startDate > endDate) {
      alert('End date cannot be before the start date.');
      return false;
    } */

    for (const employee of employeeDetails) {
      if (employee.startDate && employee.endDate && (employee.startDate < project.startDate || employee.endDate > project.endDate)) {
        alert(`Start and end dates for ${employee.user.firstname} ${employee.user.lastname} must be within the project dates.`);
        return false;
      }
      if (employee.startDate && employee.endDate && employee.startDate > employee.endDate) {
        alert(`End date cannot be before the start date for ${employee.user.firstname} ${employee.user.lastname}.`);
        return false;
      }
    }

    return true;
  };

  const renderEmployees = (employees: EmployeeProject[]) => {
    return employees.map((employee) => (
      <tr key={employee.id}>
        <td>{employee.user.firstname} {employee.user.lastname}</td>
        <td>{employee.jobDescription}</td>
        <td>{new Date(employee.startDate).toLocaleDateString()}</td>
        <td>{new Date(employee.endDate).toLocaleDateString()}</td>
      </tr>
    ));
  };

  const renderProjects = () => {
    return projects.map((project) => (
      <div key={project.id}>
        <h2>{project.name}</h2>
        <p>{project.description}</p>
        <p>Duration: {new Date(project.startDate).toLocaleDateString()} - {new Date(project.endDate).toLocaleDateString()}</p>
        <Table striped bordered hover>
          <thead>
            <tr>
              <th>Name</th>
              <th>Job Description</th>
              <th>Start Date</th>
              <th>End Date</th>
            </tr>
          </thead>
          <tbody>
            {renderEmployees(project.employeeProjects)}
          </tbody>
        </Table>
        <button className="btn btn-primary" onClick={() => editChange(project)}>Edit</button>
      </div>
    ));
  };

  return (
    <div>
      <h1>Projects</h1>
      {renderProjects()}
      {
        edit && <div>
          <div style={{
            backgroundColor: ' #ccccff'
          }}>


            <h4>{projectName}</h4>
            <form onSubmit={handleEdit}>
              <div className="mb-3">
                <label htmlFor="employees" className="form-label">
                  Select employees
                </label>
                <select
                  multiple
                  className="form-select"
                  id="employees"
                  value={selectedEmployees.map((employee) => employee.userId.toString())}
                  onChange={handleEmployeeChange}
                >
                  {employees.map((employee) => (
                    <option key={employee.userId} value={employee.userId}>
                      {employee.firstname} {employee.lastname}
                    </option>
                  ))}
                </select>
              </div>
              <button type="button" className="btn btn-primary" onClick={handleAddEmployee}>
                Add Employee
              </button>
              {employeeDetails.map((employee) => (
              <div key={employee.user.userId} className="employee-detail">
                <div className="employee-info">
                  <strong>Name:</strong> {employee.user.firstname} {employee.user.lastname}
                </div>
                <div className="employee-info">
                  <strong>Email:</strong> {employee.user.email}
                </div>
                <div className="employee-info">
                  <strong>Phone:</strong> {employee.user.phoneNumber}
                </div>
                <div className="employee-info">
                  <strong>Title:</strong> {employee.user.title ? 'Mr.' : 'Ms.'}
                </div>
                <div className="employee-info">
                  <strong>Role:</strong> {employee.user.role.name}
                </div>
                <div className="employee-info">
                  <strong>Address:</strong> {employee.user.address.street} {employee.user.address.number}, {employee.user.address.city}, {employee.user.address.country}
                </div>
                <div className="employee-info">
                  <label htmlFor={`jobDescription_${employee.user.userId}`} className="form-label">
                    Job Description:
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    id={`jobDescription_${employee.user.userId}`}
                    value={jobDescription}
                    onChange={(event) => setJobDescription(event.target.value)}
                  />
                </div>
                <div className="employee-info">
                  <label htmlFor={`startDate_${employee.user.userId}`} className="form-label">
                    Start Date:
                  </label>
                  <input
                    type="date"
                    className="form-control"
                    id={`startDate_${employee.user.userId}`}
                    value={startDate}
                    onChange={(event) => setStartDate(event.target.value)}
                  />
                </div>
                <div className="employee-info">
                  <label htmlFor={`endDate_${employee.user.userId}`} className="form-label">
                    End Date:
                  </label>
                  <input
                    type="date"
                    className="form-control"
                    id={`endDate_${employee.user.userId}`}
                    value={endDate}
                    onChange={(event) => setEndDate(event.target.value)}
                  />
                </div>
                <button type="button" className="btn btn-danger" onClick={() => handleRemoveEmployee(employee.user.userId)}>
                  Remove Employee
                </button>
              </div>
            ))}

              <button type="submit" className="btn btn-primary">
                Save
              </button>
            </form>
            <br></br>
          </div>
          <br></br><br></br>
        </div>

      }
    </div>
  );
}

export default ManagerProjects;