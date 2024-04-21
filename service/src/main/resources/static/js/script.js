function updateEmployerStatus(event) {
  const button = event.target.closest('button');
  const employerId = button.getAttribute('data-employer-id');
  const status = button.getAttribute('data-employer-status');

  fetch(`/api/v1/employers/${employerId}/status`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({status}),
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    console.log('Employer status updated successfully');
    const iconElement = event.target.closest('.card-item').querySelector(
        '.card-status-action svg');
    updateEmployerIcon(iconElement, status);
  })
  .catch(error => {
    console.error('There was a problem updating vacancy status:', error);
  });
}

function updateEmployerIcon(iconElement, status) {
  fetch(`/icons/employer-status?status=${status}`)
  .then(response => response.text())
  .then(html => {
    iconElement.outerHTML = html;
  })
  .catch(error => {
    console.error('There was a problem fetching the icon:', error);
  });
}

function updateVacancyStatus(event) {
  const button = event.target.closest('button');
  const vacancyId = button.getAttribute('data-vacancy-id');
  const status = button.getAttribute('data-vacancy-status');

  fetch(`/api/v1/vacancies/${vacancyId}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({status}),
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    console.log('Vacancy status updated successfully');
    const iconElement = event.target.closest('.card-item').querySelector(
        '.card-status-action svg');
    updateVacancyIcon(iconElement, status);
  })
  .catch(error => {
    console.error('There was a problem updating vacancy status:', error);
  });
}

function updateVacancyIcon(iconElement, status) {
  fetch(`/icons/vacancy-status?status=${status}`)
  .then(response => response.text())
  .then(html => {
    iconElement.outerHTML = html;
  })
  .catch(error => {
    console.error('There was a problem fetching the icon:', error);
  });
}
