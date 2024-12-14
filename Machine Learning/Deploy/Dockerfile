# Gunakan Python image resmi
FROM python:3.10

# Set environment variable
ENV PYTHONBUFFERED True

# Set working directory ke /app
WORKDIR /app
RUN ls
# Copy file requirements dan install dependencies
COPY requirements.txt . 
RUN pip install --no-cache-dir -r requirements.txt

# Copy semua file aplikasi (termasuk folder models)
COPY . .
RUN ls

# Jalankan aplikasi menggunakan Gunicorn
CMD ["gunicorn", "--bind", "0.0.0.0:8080", "--workers", "1", "--threads", "8", "--timeout", "0", "app:app"]
