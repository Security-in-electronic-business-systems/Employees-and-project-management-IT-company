export class User {
    id: string = '';
    email: string = '';
    name: string = '';
    surname: string = '';
    country: string = '';
    city: string = '';
    street: string = '';
    number: string = '';
    role: UserType = 0;
    accessToken: string = '';
    message: string = '';
  
    constructor(message: string, id: string, email: string, name: string, surname: string, country: string, city: string, street: string, number: string, role: UserType, token: string) {
      this.id = id;
      this.email = email;
      this.country = country;
      this.city = city;
      this.street = street;
      this.number = number;
      this.name = name;
      this.surname = surname;
      this.role = role;
      this.accessToken = token;
      this.message = message;
    }
  }

export enum UserType{
    Host = 0,
    Guest = 1,
    NK = 2
}