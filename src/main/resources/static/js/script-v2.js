document.addEventListener('DOMContentLoaded', () => {
  try {
    const ctx1 = document.getElementById('chartExpenses');
    if (ctx1) {
      new Chart(ctx1, {
        type: 'pie',
        data: {
          labels: ['Rent', 'Food', 'Transport', 'Entertainment'],
          datasets: [{
            data: [500, 300, 150, 100],
            backgroundColor: ['#0d6efd','#198754','#ffc107','#dc3545']
          }]
        }
      });
    }

    const ctx2 = document.getElementById('chartIncomeVsExpense');
    if (ctx2) {
      new Chart(ctx2, {
        type: 'bar',
        data: {
          labels: ['Jan', 'Feb', 'Mar'],
          datasets: [
            { label: 'Income', data: [3000, 3200, 3100], backgroundColor: '#0d6efd' },
            { label: 'Expenses', data: [2500, 2600, 2400], backgroundColor: '#dc3545' }
          ]
        }
      });
    }

  } catch (error) {
    console.error('Chart error:', error);
  }
});
