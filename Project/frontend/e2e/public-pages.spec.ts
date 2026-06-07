import { expect, test } from '@playwright/test';

test.describe('Docflow public UI smoke tests', () => {
  test('company registration form is visible and requires mandatory fields', async ({
    page,
  }) => {
    await page.goto('/register-company');

    await expect(
      page.getByRole('heading', { name: 'Register company' })
    ).toBeVisible();

    const submitButton = page.getByRole('button', {
      name: 'Register company',
    });

    await expect(submitButton).toBeDisabled();

    await page.getByLabel('Company name').fill('QA Company');
    await page.getByLabel('Company email').fill('qa-company@example.com');
    await page.getByLabel('Company address').fill('Test address 1');
    await page.getByLabel('Admin first name').fill('Test');
    await page.getByLabel('Admin last name').fill('Admin');
    await page.getByLabel('Admin email').fill('test-admin@example.com');

    await expect(submitButton).toBeEnabled();
  });

  test('company registration submits trimmed payload and shows success state', async ({
    page,
  }) => {
    await page.route('**/api/public/companies/register', async (route) => {
      expect(route.request().postDataJSON()).toEqual({
        companyName: 'QA Company',
        companyEmail: 'qa-company@example.com',
        companyAddress: 'Test address 1',
        adminFirstName: 'Test',
        adminLastName: 'Admin',
        adminEmail: 'test-admin@example.com',
      });

      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          code: 'COMPANY_REGISTERED',
          payload: {
            companyId: 101,
            companyName: 'QA Company',
            message: 'Initial administrator account created.',
          },
        }),
      });
    });

    await page.goto('/register-company');

    await page.getByLabel('Company name').fill('  QA Company  ');
    await page.getByLabel('Company email').fill(' qa-company@example.com ');
    await page.getByLabel('Company address').fill('  Test address 1 ');
    await page.getByLabel('Admin first name').fill(' Test ');
    await page.getByLabel('Admin last name').fill(' Admin ');
    await page.getByLabel('Admin email').fill(' test-admin@example.com ');

    await page.getByRole('button', { name: 'Register company' }).click();

    await expect(
      page.getByText('QA Company registered successfully.')
    ).toBeVisible();

    await expect(
      page.getByText('Initial administrator account created.')
    ).toBeVisible();
  });

  test('UI preview renders shared components and toastr feedback', async ({
    page,
  }) => {
    await page.goto('/ui-preview');

    await expect(
      page.getByRole('heading', { name: 'Docflow UI Components' })
    ).toBeVisible();

    await expect(page.getByText('PDF document')).toBeVisible();
    await expect(page.getByText('Ready to upload')).toBeVisible();

    await page.getByRole('button', { name: 'Success toast' }).click();

    await expect(
      page.getByText('Document uploaded successfully.')
    ).toBeVisible();
  });

  test('company registration displays backend validation error', async ({ page }) => {
    await page.route('**/api/public/companies/register', async route => {
        await route.fulfill({
        status: 409,
        contentType: 'application/json',
        body: JSON.stringify({
            code: 'COMPANY_EMAIL_ALREADY_EXISTS',
            message: 'Company email is already in use.',
        }),
        });
    });

    await page.goto('/register-company');

    await page.getByLabel('Company name').fill('QA Company');
    await page.getByLabel('Company email').fill('qa-company@example.com');
    await page.getByLabel('Company address').fill('Test address 1');
    await page.getByLabel('Admin first name').fill('Test');
    await page.getByLabel('Admin last name').fill('Admin');
    await page.getByLabel('Admin email').fill('test-admin@example.com');

    await page.getByRole('button', { name: 'Register company' }).click();

    await expect(
        page.getByText('Company email is already in use.')
    ).toBeVisible();
    });
});