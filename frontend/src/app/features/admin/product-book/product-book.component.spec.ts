import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductBookComponent } from './product-book.component';

describe('ProductBookComponent', () => {
  let component: ProductBookComponent;
  let fixture: ComponentFixture<ProductBookComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductBookComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductBookComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
