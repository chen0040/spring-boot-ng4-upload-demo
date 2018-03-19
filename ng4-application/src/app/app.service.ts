import { Injectable } from '@angular/core';
import {Http, RequestOptions, Headers,  Response} from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/map';
import {$WebSocket, WebSocketSendMode} from 'angular2-websocket/angular2-websocket';
import {AccountService} from "./account.service";


@Injectable()
export class AppService {

  private ws: $WebSocket;
  private subscribers = [];

  constructor(private _http : Http,
              private _accountService: AccountService) {

  }

  closeWebsocket() {


    this.ws.close(false);    // close
    this.ws = null;

    //this.ws.close(true);    // close immediately
  }

  onMessage(handler) {
    this.subscribers.push(handler);
  }

  connectWebsocket(token) {
    // connect
    this.ws = new $WebSocket("ws://localhost:9300/erp/websocket");
    // you can send immediately after connect,
    // data will cached until connect open and immediately send or connect fail.

    // when connect fail, websocket will reconnect or not,
    // you can set {WebSocketConfig.reconnectIfNotNormalClose = true} to enable auto reconnect
    // all cached data will lost when connect close if not reconnect

    this.ws.onOpen((msg) => {

      this.ws.getDataStream().subscribe(
        res => {
          console.log(res);
        },
        function(e) { console.log('Error: ' + e.message); },
        function() { console.log('Completed'); }
      );

      this.ws.send("CONNECT\naccept-version:1.1\nheart-beat:10000,10000\n\n\0").subscribe(
        (msg)=> {
          console.log("next", msg.data);
        },
        (msg)=> {
          console.log("error", msg);
        },
        ()=> {
          console.log("complete");
        }
      );
      var topic = "/topics/" + token + "/event";
      var id = token;
      this.ws.send("SUBSCRIBE\nid:"+id+"\ndestination:" + topic + "\n\n\0").subscribe(
        (msg)=> {
          console.log("next", msg.data);
        },
        (msg)=> {
          console.log("error", msg);
        },
        ()=> {
          console.log("complete");
        }
      );

    });


    // set received message callback
    this.ws.onMessage(
      (msg: MessageEvent)=> {
        console.log("onMessage ", msg.data);
        for(var i=0; i < this.subscribers.length; ++i) {
          this.subscribers[i](msg);
        }
      },
      {autoApply: false}
    );
  }

  uploadExcel(formData: any) {

    var headers = new Headers();

    return this._http.post('./erp/upload-excel' , formData, { headers: headers, method: 'POST' })
      .map((res: Response) => res.json());

  }

  uploadBinaryFile(formData: any) {

    var headers = new Headers();

    return this._http.post('./erp/binary-obj/upload' , formData, { headers: headers, method: 'POST' })
      .map((res: Response) => res.json());

  }

  uploadImageFile(formData: any) {

    var headers = new Headers();

    return this._http.post('./erp/image-obj/upload' , formData, { headers: headers, method: 'POST' })
      .map((res: Response) => res.json());

  }

  getImageObjIdList(): Observable<number[]> {
    const req = {
      token: this._accountService.getToken()
    };
    return this._http.post('./erp/image-obj/find-all-ids-by-tag?tag=' + 'image-' + this._accountService.getUsername(), req)
      .map((response: Response) => <number[]> response.json())
      .do(data => console.log('All: ' + JSON.stringify(data)));
  }

  getBinaryObjIdList(): Observable<number[]> {
    const req = {
      token: this._accountService.getToken()
    };
    return this._http.post('./erp/binary-obj/find-all-ids-by-tag?tag=' + 'bo-' + this._accountService.getUsername(), req)
      .map((response: Response) => <number[]> response.json())
      .do(data => console.log('All: ' + JSON.stringify(data)));
  }

  getBinaryObjById(id: number, extension: string) {
    const req = {
      token: this._accountService.getToken()
    };

    const that = this;

    Observable.create(observer => {

      let xhr = new XMLHttpRequest();

      xhr.open('POST', './erp/binary-obj/find-binary-obj-by-id?id=' + id+ '&extension=' + extension, true);
      xhr.setRequestHeader('Content-type', 'application/json');
      xhr.responseType='blob';

      xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
          if (xhr.status === 200) {
            console.log('yes');

            var contentType = "application/octet-stream";
            var blob = new Blob([xhr.response], { type: contentType });
            console.log(blob);
            observer.next(blob);
            observer.complete();
          } else {
            observer.error(xhr.response);
          }
        }
      };
      xhr.send(JSON.stringify(req));

    }).subscribe(blob => {
      console.log(blob);
      let reader = new FileReader();
      reader.onloadend = function () {
        var arrayBuffer = this.result;
        var data = new Uint8Array(arrayBuffer);

        let a = document.createElement("a");
        document.body.appendChild(a);
        //a.style = "display: none";
        var blob = new Blob([data], { type: "application/octet-stream" }), url = window.URL.createObjectURL(blob);
        a.href = url;
        a.download = that.uuid16() + extension;
        a.click();
        window.URL.revokeObjectURL(url);
      };

      reader.readAsArrayBuffer(blob);
    });

  }


  getImageObjById(id: number, extension: string) {
    const req = {
      token: this._accountService.getToken()
    };

    const that = this;

    Observable.create(observer => {

      let xhr = new XMLHttpRequest();

      xhr.open('POST', './erp/image-obj/find-image-obj-by-id?id=' + id + '&extension=' + extension, true);
      xhr.setRequestHeader('Content-type', 'application/json');
      xhr.responseType='blob';

      xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
          if (xhr.status === 200) {
            console.log('yes');

            var contentType = "application/octet-stream";
            var blob = new Blob([xhr.response], { type: contentType });

            console.log(blob);
            observer.next(blob);
            observer.complete();
          } else {
            observer.error(xhr.response);
          }
        }
      };
      xhr.send(JSON.stringify(req));

    }).subscribe(blob => {
      console.log(blob);
      let reader = new FileReader();
      reader.onloadend = function () {
        var arrayBuffer = this.result;
        var data = new Uint8Array(arrayBuffer);

        let a = document.createElement("a");
        document.body.appendChild(a);
        //a.style = "display: none";
        var blob = new Blob([data], { type: "application/octet-stream" }), url = window.URL.createObjectURL(blob);
        a.href = url;
        a.download = that.uuid16() + extension;
        a.click();
        window.URL.revokeObjectURL(url);
      };

      reader.readAsArrayBuffer(blob);
    });

  }

  uuid16 (): string {
    let uuid = '', ii;
    for (ii = 0; ii < 16; ii += 1) {
      switch (ii) {
        case 8:
        case 20:
          uuid += '-';
          uuid += (Math.random() * 16 | 0).toString(16);
          break;
        case 12:
          uuid += '-';
          uuid += '4';
          break;
        case 16:
          uuid += '-';
          uuid += (Math.random() * 4 | 8).toString(16);
          break;
        default:
          uuid += (Math.random() * 16 | 0).toString(16);
      }
    }
    return uuid;
  }

}
