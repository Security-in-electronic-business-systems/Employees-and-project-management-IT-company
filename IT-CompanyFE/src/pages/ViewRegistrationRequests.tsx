import { useState } from "react";
import { User } from "../model/user";

function ViewRegistrationRequests(){
    const [users] = useState<User[]>([]);

    return (
        <div id="bookingsTable">
          <blockquote className="blockquote text-center">
             <p className="mb-0">All registration requests:</p>
          </blockquote>
 
             <div >
					<table className="table table-striped" style={{width: '100%', alignItems:"center", marginLeft : "auto", marginRight:  "auto"}}>
                         <thead className="thead-dark">
                             <tr>
                             <th scope="col">Name</th>
                             <th scope="col">Surname</th>
                             <th scope="col">Email</th>
                             <th scope="col">Phone number</th>
                             <th scope="col">Title</th>
                             <th scope="col">Role</th>
                             <th scope="col"></th>
                             <th scope="col"></th>
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
								<td>{user.role}</td>
								<td>
									<button type="submit" className="btn btn-primary">
										Accept
									</button>
								</td>
								<td>
									<button type="submit" className="btn btn-primary">
										Decline
									</button>
								</td>
							</tr>
						))}
                            </tbody>
                     </table>
             </div>
        </div>
     )
}

export default ViewRegistrationRequests;