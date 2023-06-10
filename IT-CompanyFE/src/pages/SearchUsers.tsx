import { SyntheticEvent, useState } from "react";
import { User } from "../model/user";


let noResults = false;
let clicked = false;

function SearchUsers(){
    const [users, setUsers] = useState<User[]>([]);
    const [email, setEmail] = useState("");
    const [name, setName] = useState("");
    const [surname, setSurname] = useState("");
    const [monthNum, setMonthNum] = useState("");

    const handleSubmit = async (event: SyntheticEvent) => {
        event.preventDefault();
        await fetch("https://localhost:8081/api/v1/user/search", {
          method: "POST",
          headers: {
            "Accept": "application/json",
            "Content-Type": "application/json" 
          },
          credentials: "include",
          body: JSON.stringify({
            email: email,
            name: name,
            surname: surname,
            monthNum: monthNum,
          }),
        })
        .then((res) => {
            if (!res.ok) {
              throw new Error(`HTTP error! Status: ${res.status}`);
            }
            if (res.status === 204) {
              clicked = true;
              noResults = true;
              return;
            }else if (res.status === 200){
                noResults = false;
            }
            return res.json();
          })
          .then((data) => {
            console.log("Fetched data:", data);
            setUsers(data);
            console.log("Updated bookings:", users);
            clicked = true;
          })
          .catch((error) => {
            console.error("Fetch error:", error);
          });    
      };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        if (id === "email") {
          setEmail(value);
        }
        if (id === "name") {
            setName(value);
          }
          if (id === "surname") {
            setSurname(value);
          }
        if (id === "monthNum") {
          setMonthNum(value);
        }
      };


    return (
        <div>
          <blockquote className="blockquote text-center">
             <p className="mb-0">Search engineers:</p>
          </blockquote>
 
         <form className="col-md-6 mx-auto" onSubmit={handleSubmit}>
             <table className="table table-striped" style={{width: '110%', marginLeft : "auto", marginRight: "auto"}}>
                     <tr>
                         <th className="th-lg-percent">
                         <label className="form-label">Email </label>
                         </th>
                         <th>&nbsp;&nbsp;</th>
                         <th className="th-lg-percent">
                         <label className="form-label">Name </label>
                         </th>
                         <th>&nbsp;&nbsp;</th>
                         <th className="th-lg-percent">
                         <label className="form-label">Surname </label>
                         </th>
                         <th>&nbsp;&nbsp;</th>
                         <th className="th-lg-percent">
                         <label className="form-label">Months of experience </label>
                         </th>
                         <th>&nbsp;&nbsp;</th>
                     </tr>
                     <tr>
                         <td> 
                             <input  className="form-control" id="email" onChange={(e) => handleInputChange(e)} />
                         </td>
                         <td>&nbsp;&nbsp;</td>

                         <td> 
                             <input  className="form-control" id="name" onChange={(e) => handleInputChange(e)} />
                         </td>
                         <td>&nbsp;&nbsp;</td>

                         <td> 
                             <input  className="form-control" id="surname" onChange={(e) => handleInputChange(e)} />
                         </td>
                         <td>&nbsp;&nbsp;</td>
 
                         <td>
                             <input type="number"  className="form-control" id="monthNum" onChange={(e) => handleInputChange(e)} />
                         </td>
                         <td>&nbsp;&nbsp;</td>

                         <td>
                         
                         </td>
                         <button type="submit" className="btn btn-primary">Search</button>
                     </tr>
                     <tr>&nbsp;</tr>
                     <tr>&nbsp;</tr>
                 </table>
             </form>
 
             {noResults && clicked && <div style={{ color: 'blue' }} >There are no users for given inputs!</div>}
 
             {noResults === false && clicked && <div >
                     <table className="table table-striped" style={{width: '100%', alignItems:"center", marginLeft : "auto", marginRight:  "auto"}}>
                         <thead className="thead-dark">
                             <tr>
                             <th scope="col">Firstname</th>
                             <th scope="col">Lastname</th>
                             <th scope="col">Email</th>
                             <th scope="col">PhoneNumber</th>
                             <th scope="col">Title</th>
                             <th scope="col">Address</th>
                             <th scope="col">Role</th>
                             </tr>
                         </thead>
                         <tbody>
                             {users.map((user) => (
                             <tr key={user.email}>
                                 <td>{user.firstname}</td>
                                 <td>{user.lastname}</td>
                                 <td>{user.email}</td>
                                 <td>{user.phoneNumber}</td>
                                 <td>{user.title}</td>
                                 <td>{user.address.country + " " + user.address.city + " " + user.address.street + " " + user.address.number}</td>
                                 <td>{user.role.name}</td>
                             </tr>
                             ))}
                         </tbody>
                     </table>
             </div>}
        </div>
     )
}

export default SearchUsers;