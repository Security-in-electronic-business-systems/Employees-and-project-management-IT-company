import { SyntheticEvent, useEffect, useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import Table from 'react-bootstrap/Table';
import { FaEdit } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';

interface Project {
  id: number;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  employeeProjects: EmployeeProject[];
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

function EmployeeProjects ()  {
  const [projects, setProjects] = useState<Project[]>([]);
  const navigate = useNavigate();

  const [edit, setEdit] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  
  const[jobDescription,setJobDescription] = useState('');
  const[projectName,setprojectName] = useState('');
  const[id, setId]= useState(-1);

  useEffect(() => {
    fetch('https://localhost:8081/api/v1/user/getEmployeProjects', {
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

  const editChange =(employees: EmployeeProject[], project: Project):void=>{
    setprojectName(project.name)
    setEdit(true);
    setIsEditMode(true);
    employees.map((employee) => (
      setJobDescription(employee.jobDescription),
      setId(employee.id)
    ));
    
  }

  const handleEdit = async (event: SyntheticEvent) => {
    event.preventDefault();


    await fetch('https://localhost:8081/api/v1/user/editJobDescription', {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        id:id,
        jobDescription: jobDescription,
      }),
    })
      .then((response) => {
        if (response.ok) {
          setEdit(false); // Isključite način izmene nakon uspešnog čuvanja
        }
      })
      .catch((error) => console.log(error));
      navigate("/employeeProjects");
      window.location.reload()

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
          <button className="btn btn-primary" onClick={() =>editChange(project.employeeProjects, project)}>Edit</button>
        </Table>
       
      </div>
    ));
  };

  return (
    <div>
      <h1>Projects</h1>
      {renderProjects()}
      {
        edit&&<div>
        <div  style={{
          backgroundColor: ' #ccccff'
        }}>
        
        <div className="row mb-3">
        <div className="col">
          <label className="form-label" htmlFor="name">
            <FaEdit className="me-2" /> Project name
          </label>
          <input
            className="form-control"
            id="name"
            value={projectName}
            onChange={(event) => setJobDescription(event.target.value)}
            required
            disabled // Onemogućite unos u načinu prikaza
          />
        </div>
        </div>
          
        <div className="row mb-3">
        <div className="col">
          <label className="form-label" htmlFor="job">
            <FaEdit className="me-2" /> Job description
          </label>
          <input
            className="form-control"
            id="job"
            value={jobDescription}
            onChange={(event) => setJobDescription(event.target.value)}
            required
            disabled={!isEditMode} // Onemogućite unos u načinu prikaza
          />
        </div>
        
        </div>
        <button onClick={handleEdit} className="btn btn-primary">
          Save
        </button>
        <br></br>
        </div>
        <br></br><br></br>
        </div>
        
      }
    </div>
  );
}

export default EmployeeProjects;
