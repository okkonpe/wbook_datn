import { Routes } from '@angular/router';
// import { HeaderComponent } from './app/components/home/header.component';
import { HeaderComponent } from './components/home/header/header.component';
import { ProductComponent } from './components/product/product.component';
import { ContactComponent } from './components/home/contact/contact.component';


export const routes: Routes = [ 
    { path: 'product', component: ProductComponent },
    {path:'contact',component:ContactComponent}

];
