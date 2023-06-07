import React, { useState, useEffect, ChangeEvent } from 'react';

interface EmployeeDetail {
  user: User;
  jobDescription: string;
  startDate: string;
  endDate: string;
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

interface ProjectDTO {
  name: string;
  startDate: string;
  endDate: string;
  description: string;
  employeeProjects: EmployeeDetail[];
}

function CreateProject() {
  const [projectName, setProjectName] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [description, setDescription] = useState('');
  const [employees, setEmployees] = useState<User[]>([]);
  const [selectedEmployees, setSelectedEmployees] = useState<User[]>([]);
  const [employeeDetails, setEmployeeDetails] = useState<EmployeeDetail[]>([]);

  useEffect(() => {
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
      .catch((error) =>
       console.log(error) );
  }, []);

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

  const handleJobDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>, employeeId: number) => {
    const { value } = event.target;
    setEmployeeDetails((prevEmployeeDetails) =>
      prevEmployeeDetails.map((employee) =>
        employee.user.userId === employeeId ? { ...employee, jobDescription: value } : employee
      )
    );
  };

  const handleStartDateChange = (event: React.ChangeEvent<HTMLInputElement>, employeeId: number) => {
    const { value } = event.target;
    setEmployeeDetails((prevEmployeeDetails) =>
      prevEmployeeDetails.map((employee) =>
        employee.user.userId === employeeId ? { ...employee, startDate: value } : employee
      )
    );
  };

  const handleEndDateChange = (event: React.ChangeEvent<HTMLInputElement>, employeeId: number) => {
    const { value } = event.target;
    setEmployeeDetails((prevEmployeeDetails) =>
      prevEmployeeDetails.map((employee) =>
        employee.user.userId === employeeId ? { ...employee, endDate: value } : employee
      )
    );
  };

  const validateDates = (): boolean => {
    if (startDate && endDate && startDate > endDate) {
      alert('End date cannot be before the start date.');
      return false;
    }

    for (const employee of employeeDetails) {
      if (employee.startDate && employee.endDate && (employee.startDate < startDate || employee.endDate > endDate)) {
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

  const handleCreateProject = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!validateDates()) {
      return;
    }

    const projectDTO: ProjectDTO = {
      name: projectName,
      startDate: startDate,
      endDate: endDate,
      description: description,
      employeeProjects: employeeDetails.map((employee) => {
        return {
          user: employee.user,
          jobDescription: employee.jobDescription,
          startDate: employee.startDate,
          endDate: employee.endDate,
        };
      }),
    };

    try {
      const response = await fetch('https://localhost:8081/api/v1/user/createProject', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(projectDTO),
      });

      if (response.ok) {
        // Projekt je uspešno kreiran
        window.alert('Project created successfully.');
        // Resetovanje polja forme
        setProjectName('');
        setStartDate('');
        setEndDate('');
        setDescription('');
        setEmployeeDetails([]);
      } else {
        // Greška pri kreiranju projekta
        if (response.status==403)
          window.alert("You don't have permission!")
        console.error('Error creating project:', response.statusText);
      }
    } catch (error) {
      console.error('Error:', error);
    }
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

  const handleRemoveEmployee = (employeeId: number) => {
    setEmployeeDetails((prevEmployeeDetails) => prevEmployeeDetails.filter((employee) => employee.user.userId !== employeeId));
  };

  return (
    <div>
      <h1>Create Project</h1>
      <form onSubmit={handleCreateProject}>
        <div className="mb-3">
          <label htmlFor="projectName" className="form-label">
            Project Name
          </label>
          <input
            type="text"
            className="form-control"
            id="projectName"
            value={projectName}
            onChange={(e) => setProjectName(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="startDate" className="form-label">
            Start Date
          </label>
          <input
            type="date"
            className="form-control"
            id="startDate"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="endDate" className="form-label">
            End Date
          </label>
          <input
            type="date"
            className="form-control"
            id="endDate"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="description" className="form-label">
            Description
          </label>
          <textarea
            className="form-control"
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
          ></textarea>
        </div>
        <div className="mb-3">
          <label htmlFor="employees" className="form-label">
            Employees
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
        {employeeDetails.length > 0 && (
          <div className="mt-3">
            <h4>Added Employees</h4>
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
                    value={employee.jobDescription}
                    onChange={(e) => handleJobDescriptionChange(e, employee.user.userId)}
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
                    value={employee.startDate}
                    onChange={(e) => handleStartDateChange(e, employee.user.userId)}
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
                    value={employee.endDate}
                    onChange={(e) => handleEndDateChange(e, employee.user.userId)}
                  />
                </div>
                <button type="button" className="btn btn-danger" onClick={() => handleRemoveEmployee(employee.user.userId)}>
                  Remove Employee
                </button>
              </div>
            ))}
          </div>
        )}
        <button type="submit" className="btn btn-primary">
          Create Project
        </button>
      </form>
    </div>
  );
}

export default CreateProject;
