import csv
import random
from datetime import datetime, timedelta

filing_methods = ["E-File", "Paper"]
complexities = ["Low", "Medium", "High"]
errors_flag = ["Yes", "No"]
refund_types = ["Direct Deposit", "Check"]
backlog_flag = ["Yes", "No"]
bank_methods = ["ACH", "Wire", "N/A"]

def random_date(start_date="2025-01-01", end_date="2025-04-30"):
    start = datetime.strptime(start_date, "%Y-%m-%d")
    end = datetime.strptime(end_date, "%Y-%m-%d")
    delta = end - start
    random_days = random.randint(0, delta.days)
    return start + timedelta(days=random_days)

with open("tax_refund_training.csv", "w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow([
        "Filing_ID","Filing_Method","Submission_Date","Return_Complexity",
        "Errors_Flag","Refund_Type","IRS_Backlog_Flag","Bank_Deposit_Method",
        "Refund_Amount","Actual_Refund_Days",
        "Return_Complexity_Score","Seasonal_Filing_Indicator",
        "Filing_Day_Of_Week","Refund_Amount_Bucket","Error_Severity_Score"
    ])
    
    for i in range(1, 100001):
        filing_method = random.choice(filing_methods)
        complexity = random.choice(complexities)
        errors = random.choice(errors_flag)
        refund_type = random.choice(refund_types)
        backlog = random.choice(backlog_flag)
        bank_method = "N/A" if refund_type == "Check" else random.choice(bank_methods)
        refund_amount = random.randint(300, 5000)
        
        # Refund days logic
        if filing_method == "E-File":
            base_days = random.randint(7, 15)
        else:
            base_days = random.randint(25, 45)
        if errors == "Yes":
            base_days += random.randint(5, 15)
        if backlog == "Yes":
            base_days += random.randint(5, 10)
        
        # Engineered features
        complexity_score = {"Low":1,"Medium":2,"High":3}[complexity]
        submission_date = random_date()
        seasonal_flag = 1 if submission_date.month in [2,3,4] else 0
        day_of_week = submission_date.strftime("%A")
        amount_bucket = "Small" if refund_amount < 1000 else "Medium" if refund_amount <= 2500 else "Large"
        error_score = 1 if errors == "Yes" else 0
        
        writer.writerow([
            i, filing_method, submission_date.strftime("%Y-%m-%d"), complexity, errors,
            refund_type, backlog, bank_method, refund_amount, base_days,
            complexity_score, seasonal_flag, day_of_week, amount_bucket, error_score
        ])
print("Training data 'tax_refund_training.csv' generated successfully.")