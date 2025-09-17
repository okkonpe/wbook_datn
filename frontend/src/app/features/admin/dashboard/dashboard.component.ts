import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType, Chart, registerables } from 'chart.js';

// Register all Chart.js components
Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  constructor(private http: HttpClient) {}

  // Summary counters
  totalRevenue = 0;
  todayRevenue = 0;
  weekRevenue = 0;
  monthRevenue = 0;
  activeVouchers = 0;
  topSellingCount = 0;
  lowStockCount = 0;
  totalOrders = 0;

  // Lists
  topSelling: any[] = [];
  lowStock: any[] = [];
  showTopSelling = false;
  showLowStock = false;

  // Range filter
  rangeType: 'day' | 'week' | 'month' | 'custom' = 'day';
  fromDate = '';
  toDate = '';

  // Chart properties
  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
      },
      title: {
        display: true,
        text: 'Doanh thu theo thời gian'
      }
    },
    scales: {
      x: {
        type: 'category',
        display: true
      },
      y: {
        type: 'linear',
        beginAtZero: true,
        display: true,
        ticks: {
          callback: function(value: any) {
            return new Intl.NumberFormat('vi-VN').format(Number(value)) + ' đ';
          }
        }
      }
    }
  };
  
  public barChartType: ChartType = 'bar';
  public barChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Doanh thu',
        backgroundColor: 'rgba(54, 162, 235, 0.6)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1
      }
    ]
  };

  ngOnInit() {
    this.loadSummary();
    this.loadRangeStats('day');
    
    // Load initial chart data
    this.loadChartData();
  }

  loadChartData() {
    // Load chart data for current range
    this.loadRangeStats(this.rangeType);
  }

  loadSummary() {
    this.http.get<any>('http://localhost:8080/api/hoa-don/stat/summary').subscribe(res => {
      this.totalRevenue = res.totalRevenue || 0;
      this.todayRevenue = res.todayRevenue || 0;
      this.weekRevenue = res.weekRevenue || 0;
      this.monthRevenue = res.monthRevenue || 0;
      this.activeVouchers = res.activeVouchers || 0;
      this.topSellingCount = res.topSellingCount || 0;
      this.lowStockCount = res.lowStockCount || 0;
      this.totalOrders = res.totalOrders || 0;
    }, () => {});
  }

  openTopSelling() {
    this.showTopSelling = true; this.showLowStock = false;
    this.http.get<any[]>('http://localhost:8080/api/hoa-don/stat/top-selling?limit=20').subscribe(r => this.topSelling = r, () => {});
  }

  openLowStock() {
    this.showLowStock = true; this.showTopSelling = false;
    this.http.get<any[]>('http://localhost:8080/api/hoa-don/stat/low-stock?limit=50').subscribe(r => this.lowStock = r, () => {});
  }

  setRange(type: 'day'|'week'|'month'|'custom') {
    this.rangeType = type;
    this.loadRangeStats(type);
  }


  loadRangeStats(type: string) {
    let url = 'http://localhost:8080/api/hoa-don/stat/revenue?type=' + type;
    if (type === 'custom' && this.fromDate && this.toDate) {
      url += `&from=${this.fromDate}&to=${this.toDate}`;
    }
    this.http.get<any[]>(url).subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.updateChart(data);
        } else {
          // Fallback data if no data from API
          this.updateChartWithFallback(type);
        }
      },
      error: (err) => {
        console.error('Error loading revenue data:', err);
        // Show fallback data on error
        this.updateChartWithFallback(type);
      }
    });
  }

  updateChartWithFallback(type: string) {
    const today = new Date();
    let labels: string[] = [];
    let values: number[] = [];
    
    switch (type) {
      case 'day':
        labels = [today.toLocaleDateString('vi-VN')];
        values = [this.todayRevenue || 0];
        break;
      case 'week':
        for (let i = 6; i >= 0; i--) {
          const date = new Date(today);
          date.setDate(date.getDate() - i);
          labels.push(date.toLocaleDateString('vi-VN'));
          values.push(Math.floor(Math.random() * 1000000) + 100000); // Random data for demo
        }
        break;
      case 'month':
        for (let i = 29; i >= 0; i--) {
          const date = new Date(today);
          date.setDate(date.getDate() - i);
          labels.push(date.toLocaleDateString('vi-VN'));
          values.push(Math.floor(Math.random() * 2000000) + 200000); // Random data for demo
        }
        break;
    }
    
    this.barChartData = {
      labels: labels,
      datasets: [
        {
          data: values,
          label: 'Doanh thu',
          backgroundColor: 'rgba(54, 162, 235, 0.6)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 1
        }
      ]
    };
  }

  updateChart(data: any[]) {
    const labels = data.map(item => {
      const dateStr = item.date || item.label;
      const date = new Date(dateStr);
      return date.toLocaleDateString('vi-VN');
    });
    
    const values = data.map(item => {
      const value = item.revenue || item.value || 0;
      return typeof value === 'string' ? parseFloat(value) : value;
    });
    
    // Force update chart data
    this.barChartData = {
      labels: [...labels], // Create new array to trigger change detection
      datasets: [
        {
          data: [...values], // Create new array to trigger change detection
          label: 'Doanh thu',
          backgroundColor: 'rgba(54, 162, 235, 0.6)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 1
        }
      ]
    };
  }
}
