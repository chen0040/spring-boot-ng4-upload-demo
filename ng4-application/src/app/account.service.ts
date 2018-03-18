import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/map';

import { IAccount } from './account';
import { IToken } from './token';
import { ILoginObj } from './login-obj';

@Injectable()
export class AccountService {

    authenticated: boolean = false;
    account: IAccount;
    username: string = "anonymous";

    constructor(private _http : Http) {

    }

    getToken(): string {
        var token = localStorage.getItem('token');
        if(!token) token = '';
        return token;
    }




    validate(): Observable<IToken> {
        var token = localStorage.getItem('token');
        if(!token) token = '';

        let body = {
          token: token,
          username: ''
        };
        return this._http.post('./erp/validate-api-json', body)
            .map((response: Response) => <IToken> response.json())
            .do(data => {
              console.log('Token Obj: ' + JSON.stringify(data));
              this.authenticated = data.username && data.username != '';
              if(this.authenticated) {
                this.username = data.username;
              } else {
                this.username = "anonymous";
              }
            })
            .catch(this.handleError);
    }

    authenticate(loginObj: ILoginObj) : Observable<IAccount> {
        return this._http.post('./erp/login-api-json', loginObj)
            .map((response: Response) => <IAccount> response.json())
            .do(data => {
              console.log('Authenticatation: ' + JSON.stringify(data));
              if(!data.error || data.error == ''){
                this.authenticated = true;
                localStorage.setItem('token', data.token);
                this.account = data;
                this.username = data.username;
              } else {
                this.username = "anonymous";
              }
            })
            .catch(this.handleError);
    }

    logout(): Observable<IToken> {
        var token = localStorage.getItem('token');
        if(!token) token = '';

        let tokenObj = {
          token: token,
          username: ''
        };
        return this._http.post('./erp/logout-api-json', tokenObj)
            .map((response: Response) => <IToken> response.json())
            .do(data => {
              console.log('Logout: ' + JSON.stringify(data));
              localStorage.setItem('token', data.token);
              localStorage.setItem('cartId', '');
              this.account = null;
              this.username = "anonymous";
            })
            .catch(this.handleError);
    }

    private handleError(error: Response) {
        console.error(error);
        return Observable.throw(error.json().error || 'Server error');
    }

}
