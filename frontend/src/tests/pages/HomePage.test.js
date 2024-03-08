import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

import HomePage from "main/pages/HomePage";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useParams: () => ({
        commonsId: 1
    }),
    useNavigate: () => mockNavigate
}));

describe("HomePage tests", () => {
    const queryClient = new QueryClient();
    const axiosMock = new AxiosMockAdapter(axios);

    beforeEach(() => {
        jest.clearAllMocks();
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    });

    afterEach(() => {
        axiosMock.reset();
      });

    test("expected CSS properties", () => {
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <HomePage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        const title = screen.getByTestId("homePage-title");
        expect(title).toHaveAttribute("style", "font-size: 75px; border-radius: 7px; background-color: white; opacity: 0.9;");
    });

    test('shows greeting for logged-in users with dynamic time of day', () => {
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly); // Adjust this if needed
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <HomePage />
                </MemoryRouter>
            </QueryClientProvider>
        );
    
        const greetingElement = screen.getByTestId("homePage-title");
        expect(
            greetingElement.textContent
        ).toMatch(/Good (morning|afternoon|evening), cgaucho/); 
    });
    
    test('renders greeting for non-logged-in users correctly', () => {
        // Mock the `useCurrentUser` hook to return a logged-out state
        jest.mock("main/utils/currentUser", () => ({
            useCurrentUser: jest.fn(() => ({ data: { loggedIn: false } }))
        }));
    
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <HomePage />
                </MemoryRouter>
            </QueryClientProvider>
        );
    
        const greetingElement = screen.getByTestId("homePage-title");
        expect(greetingElement.textContent).toContain("Good afternoon, cgaucho");
    });  


describe('HomePage greetings for logged-in users at different times of the day', () => {
    const originalDate = global.Date;
    const axiosMock = new AxiosMockAdapter(axios);
    const queryClient = new QueryClient();

    const mockDateWithHour = (hour) => {
      global.Date = class extends Date {
        constructor() {
          super();
        }
        getHours() {
          return hour;
        }
        getDate() { return originalDate.now(); }
        getMonth() { return originalDate.now(); }
        getFullYear() { return originalDate.now(); }
      };
    };
  
    afterEach(() => {
      global.Date = originalDate;
    });
  afterEach(() => {
  axiosMock.reset();
});
    const testCases = [
      { hour: 9, expectedGreeting: "Good morning, cgaucho" },
      { hour: 14, expectedGreeting: "Good afternoon, cgaucho" },
      { hour: 19, expectedGreeting: "Good evening, cgaucho" },
      { hour: 12, expectedGreeting: "Good morning, cgaucho" },
      { hour: 18, expectedGreeting: "Good afternoon, cgaucho" },
    ];
  
    testCases.forEach(({ hour, expectedGreeting }) => {
      test(`shows "${expectedGreeting}" for logged-in user at ${hour} hours`, () => {
        mockDateWithHour(hour);
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        render(
          <QueryClientProvider client={queryClient}>
            <MemoryRouter>
              <HomePage />
            </MemoryRouter>
          </QueryClientProvider>
        );
  
        const greetingElement = screen.getByTestId("homePage-title");
        expect(greetingElement.textContent).toMatch(new RegExp(expectedGreeting, 'i'));
      });
    });
  });
  