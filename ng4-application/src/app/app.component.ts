import {Component, OnInit} from '@angular/core';
import { AppService } from './app.service';
import {AccountService} from "./account.service";
import {ILoginObj} from "./login-obj";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Spring WebSocket Demo with Angular 4';
  messages = [];
  errorMessage: string;
  authenticated: boolean;
  loginInProcess: boolean = false;
  loginObj: ILoginObj = {
    username: '',
    password: ''
  };
  excelUpload = {
    FilePath: ''
  };
  binaryObjUpload = {
    FilePath: ''
  };
  imageObjUpload = {
    FilePath: ''
  };
  imageObjList : number[] = [];
  binaryObjList : number[] = [];

  constructor(private _appService: AppService,
              private _accountService: AccountService) {

  }


  ngOnInit(): void {
    this._appService.onMessage((message: MessageEvent) => {
      var data = message.data;
      this.messages.push(data);
      if(this.messages.length > 100) {
        this.messages.slice(0, 1);
      }
    });

    this._accountService.validate().subscribe(tokenObj => {
        this.loginObj.username = tokenObj.username;
        this.authenticated = this._accountService.authenticated;
        this._appService.connectWebsocket(this._accountService.getToken());
        this.loadBinaryObjList();
        this.loadImageObjList();
      },
      error => this.errorMessage = <any>error);


  }

  login(): void {
    this.loginInProcess = true;
  }

  loginConfirm(): void {
    this.loginInProcess = false;
    this._accountService.authenticate(this.loginObj).subscribe(account => {
        if(!this._accountService.authenticated){
          alert(account.error);
        } else {
          this.loginObj.password = '';
          this.authenticated = true;
          this._appService.connectWebsocket(this._accountService.getToken());
        }
      },
      error => this.errorMessage = <any>error);
  }

  loginCancel(): void {
    this.loginObj.password = '';
    this.loginInProcess = false;
  }

  logout(): void {
    this._accountService.logout().subscribe(token => {
        this.loginObj.username = '';
        this.authenticated = false;
        this._appService.closeWebsocket()
      },
      error => this.errorMessage = <any>error);
  }

  loadImageObjList() {
    this._appService.getImageObjIdList().subscribe(idList => {
      this.imageObjList = idList;
    })
  }

  loadBinaryObjList() {
    this._appService.getBinaryObjIdList().subscribe(idList => {
      this.binaryObjList = idList;
    })
  }

  downloadBinaryObj(binaryObjId: number) {
    this._appService.getBinaryObjById(binaryObjId, '.pdf');
  }

  downloadImageObj(imageObjId: number) {
    this._appService.getImageObjById(imageObjId, '.png');
  }

  binaryFileChange(event) {
    let fileList: FileList = event.target.files;
    if(fileList.length > 0) {
      let file: File = fileList[0];
      let fileSize:number=fileList[0].size;
      if(fileSize<=10485760)
      {
        let formData:FormData = new FormData();
        formData.append('id', '0');
        formData.append('file',file);
        formData.append('token',this._accountService.getToken());
        formData.append('secret', 'wizlah');
        formData.append('tag', 'bo-' + this._accountService.getUsername());
        this._appService.uploadBinaryFile(formData).subscribe(val => {
          alert(val);
          this.loadBinaryObjList();
        });
      }
      else
      {
        alert("File size is exceeded");
      }
    }
    else
    {
      alert("Something went Wrong.");
    }
  }

  imageFileChange(event) {
    let fileList: FileList = event.target.files;
    if(fileList.length > 0) {
      let file: File = fileList[0];
      let fileSize:number=fileList[0].size;
      if(fileSize<=10485760)
      {
        let formData:FormData = new FormData();
        formData.append('id', '0');
        formData.append('file',file);
        formData.append('token',this._accountService.getToken());
        formData.append('parentId', '-1');
        formData.append('secret', 'wizlah');
        formData.append('tag', 'image-' + this._accountService.getUsername());
        this._appService.uploadImageFile(formData).subscribe(val => {
          alert(val);
          this.loadImageObjList();
        });
      }
      else
      {
        alert("File size is exceeded");
      }
    }
    else
    {
      alert("Something went Wrong.");
    }
  }



  excelFileChange(event) {
    let fileList: FileList = event.target.files;
    if(fileList.length > 0) {
      let file: File = fileList[0];
      let fileSize:number=fileList[0].size;
      if(fileSize<=10485760)
      {
        let formData:FormData = new FormData();
        formData.append('file',file);
        formData.append('token',this._accountService.getToken());
        this._appService.uploadExcel(formData).subscribe(val => {
          alert(val);
        });
      }
      else
      {
        alert("File size is exceeded");
      }
    }
    else
    {
      alert("Something went Wrong.");
    }
  }


}
