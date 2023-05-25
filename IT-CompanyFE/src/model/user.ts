export class User {
    firstname = '';
    lastname='';
    email='';
    password='';
    phoneNumber='';
    title = '';
    address : Address = new Address('','','','');
    role:Role = new Role('');
  
    constructor(firstname: string, lastname: string, email: string, password: string, phoneNumber: string, title: string, role: Role) {
      this.firstname = firstname;
      this.lastname = lastname;
      this.email = email;
      this.password = password;
      this.phoneNumber = phoneNumber;
      this.title = title;
      this.role = role;
    }
  }

export enum UserType{
    SOFTWARE_ENGINEER = 0,
    PROJECT_MANAGER = 1,
    HR_MANAGER = 2,
    ADMINISTRATOR = 3,
    UNAUTHENTICATED_USER = 4
}

export class Address{
    country='';
    city='';
    street='';
    number='';
    constructor(country:string, city:string, street:string, number:string){
        this.country = country;
        this.city = city;
        this.street = street;
        this.number = number;
    }
}

export class Role{
  name='';
  constructor(name:string){
    this.name=name;
  }
}