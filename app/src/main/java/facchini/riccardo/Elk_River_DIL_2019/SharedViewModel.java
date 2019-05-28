package facchini.riccardo.Elk_River_DIL_2019;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import facchini.riccardo.Elk_River_DIL_2019.Customer_Package.Customer;
import facchini.riccardo.Elk_River_DIL_2019.Employee_Package.Employee;

public class SharedViewModel extends ViewModel
{
    private MutableLiveData<Employee> currentEmployee = new MutableLiveData<>();
    private MutableLiveData<Customer> currentCustomer = new MutableLiveData<>();
    
    public void setCurrentEmployee(Employee s)
    {
        currentEmployee.setValue(s);
    }
    
    public void setCurrentCustomer(Customer c) {currentCustomer.setValue(c);}
    
    public Employee getCurrentEmployee()
    {
        return currentEmployee.getValue();
    }
    
    public Customer getCurrentCustomer() {return currentCustomer.getValue();}
    
    
}
