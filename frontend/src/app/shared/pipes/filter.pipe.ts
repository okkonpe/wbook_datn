import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filter',
  standalone: true
})
export class FilterPipe implements PipeTransform {
  transform(array: any[], field: string, value: any): any[] {
    if (!array || !field) {
      return array;
    }
    
    return array.filter(item => item[field] === value);
  }
}
