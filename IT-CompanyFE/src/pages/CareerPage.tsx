import React, { SyntheticEvent, useEffect, useState } from 'react';
import { UseLoggedUser } from '../hooks/UseLoggedUserInformation';
import { FaEdit } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';


interface SkillDTO {
  name: string;
  grade: number;
  email: string;
}
interface Skill {
  id: number;
  name: string;
  grade: number;
}


function CareerPage() {
  const [name, setName] = useState('');
  const [grade, setGrade] = useState(0);
  const [skills, setSkills] = useState<Skill[]>([]);
  const navigate = useNavigate();

  const [edit, setEdit] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [id, setId] = useState(-1);

  useEffect(() => {
    
    fetch('https://localhost:8081/api/v1/user/getAllSkill', {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
      },
      credentials: 'include',
    })
      .then((response) => response.json())
      .then((data) => {
        setSkills(data);
      })
      .catch((error) => console.log(error));
  }, []);
  
  const editChange =(skill: Skill):void=>{
    
    setEdit(true);
    setIsEditMode(true);
    setName(skill.name);
    setGrade(skill.grade);
    setId(skill.id)
    
  }

  const handleAddSkill = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();


    const skillDTO: SkillDTO = {
      name: name,
      grade: grade,
      email: UseLoggedUser()?.email || ' '
    };

    try {
      const response = await fetch('https://localhost:8081/api/v1/user/addSkill', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(skillDTO),
      });

      if (response.ok) {
        // Projekt je uspešno kreiran
        window.alert('Project created successfully.');
        // Resetovanje polja forme
        setName('');
        setGrade(0);
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


  const handleEdit = async (event: SyntheticEvent) => {
    event.preventDefault();


    await fetch('https://localhost:8081/api/v1/user/editSkill', {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        id: id,
        name: name,
        grade: grade
      }),
    })
      .then((response) => {
        if (response.ok) {
          setEdit(false); // Isključite način izmene nakon uspešnog čuvanja
        }
      })
      .catch((error) => console.log(error));
      navigate("/careerPage");
      window.location.reload()

  };



  return (
    <div>
      <h1>Your skill</h1>
      <div>
      <table className="table table-striped">
              <thead>
                <tr>
                <th scope="col">Skill</th><th scope="col">Grade</th><th></th>
                </tr>
              </thead>
             {skills.map((skill) => (
              
              <tbody>
                 <tr key={skill.id}>
                   <td>{skill.name}</td>
                   <td>{skill.grade}</td>
                   <td>{<button className="btn btn-primary" onClick={() =>editChange(skill)}>Edit</button>}</td>
                 </tr>
               </tbody>
            
						))}
      </table>
      </div>
      <br></br><br></br><br></br>{
        edit&&<div>
        <div  style={{
          backgroundColor: ' #ccccff'
        }}>
        <div className="row mb-3">
        <div className="col">
          <label className="form-label" htmlFor="name">
            <FaEdit className="me-2" /> Name
          </label>
          <input
            className="form-control"
            id="name"
            value={name}
            onChange={(event) => setName(event.target.value)}
            required
            disabled // Onemogućite unos u načinu prikaza
          />
        </div></div>
        <div className="row mb-3">
        <div className="col">
          <label className="form-label" htmlFor="grade">
            Grade(1-5)
          </label>
          <input
            className="form-control"
            id="name"
            value={grade}
            onChange={(event) => setGrade(Number(event.target.value))}
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
      
      <h1>Add skill</h1>
      <form onSubmit={handleAddSkill}>
        <div className="mb-3">
          <div className="mb-3" style={{width : "40%"}}>
            <label htmlFor="Name" className="form-label">
              Skill Name
            </label>
            <input
              type="text"
              className="form-control"
              id="Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>
        
          <div className="mb-3" style={{width : "40%"}}>
            <label htmlFor="grade" className="form-label">
              Grade(1-5)
            </label>
            <input
              type="number"
              className="form-control"
              id="Grade"
              value={grade}
              onChange={(e) => setGrade(Number(e.target.value))}
              min={1}
              max={5}
              required
            />
          </div>
        </div>

        <button type="submit" className="btn btn-primary">
          AddSkill
        </button>
    
      </form>
    </div>
  );
}

export default CareerPage;
