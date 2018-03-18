import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { AccountService } from './account.service';

@NgModule({
    declarations: [
    ],
    imports: [
        FormsModule,
        BrowserModule,
        RouterModule.forChild([

        ])
    ],
    providers: [
        AccountService,
    ]
})
export class AccountModule { }
